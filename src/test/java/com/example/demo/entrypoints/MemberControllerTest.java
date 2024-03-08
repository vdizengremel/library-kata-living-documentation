package com.example.demo.entrypoints;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberStatus;
import com.example.demo.infrastructure.MemberInMemoryRepository;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"inMemoryRepository"})
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ComponentScan(basePackages = "com.example.demo")
class MemberControllerTest {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private MemberInMemoryRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    public void shouldAddMemberAndReturn200() throws JSONException {
        HttpEntity<String> requestEntity = httpEntityFomJson("""
                {
                    "firstName": "Jean",
                    "lastName": "Dupond",
                    "email": "jean.dupond@somemail.com"
                }
                """);

        ResponseEntity<String> response = template.postForEntity("/member/", requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String expectedBody = """
                {
                    "id": "001b0068-1eb5-4c65-85c4-1b1eb788ecd5",
                    "firstName": "Jean",
                    "lastName": "Dupond",
                    "email": "jean.dupond@somemail.com",
                    "status": "NEW_MEMBER"
                }
                """;
        JSONAssert.assertEquals(expectedBody, response.getBody(), JSONCompareMode.STRICT);

        assertThat(memberRepository.countAll()).isEqualTo(1);

        MemberId id = MemberInMemoryRepository.MEMBER_IDS.getFirst();

        assertThat(memberRepository.findById(id)).hasValueSatisfying(member -> {
            Member expectedMember = new Member(id, "Jean", "Dupond", "jean.dupond@somemail.com", MemberStatus.NEW_MEMBER);
            assertThat(member).usingRecursiveComparison().isEqualTo(expectedMember);
        });
    }

    @Test
    public void shouldNotAddMemberAndReturn400WhenAlreadyExistsWithSameEmail() {
        HttpEntity<String> requestEntity = httpEntityFomJson("""
                {
                    "firstName": "Jean",
                    "lastName": "Dupond",
                    "email": "jean.dupond@somemail.com"
                }
                """);

        template.postForEntity("/member/", requestEntity, String.class);
        ResponseEntity<String> response = template.postForEntity("/member/", requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(memberRepository.countAll()).isEqualTo(1);
    }

    @NotNull
    private static HttpEntity<String> httpEntityFomJson(@Language("JSON") String requestJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return new HttpEntity<>(requestJson, headers);
    }
}
