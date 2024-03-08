package com.example.demo.entrypoints;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberStatus;
import com.example.demo.infrastructure.MemberInMemoryRepository;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration")
@ActiveProfiles({"inMemoryRepository"})
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private MemberInMemoryRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Nested
    class PostMember {
        @Test
        public void shouldNotAddMemberAndReturn400WhenAlreadyExistsWithSameEmail() {
            HttpEntity<String> requestEntity = HttpEntityFactory.httpEntityFomJson("""
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

        @Test
        public void shouldAddMemberAndReturn200() throws JSONException {
            HttpEntity<String> requestEntity = HttpEntityFactory.httpEntityFomJson("""
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
    }
}
