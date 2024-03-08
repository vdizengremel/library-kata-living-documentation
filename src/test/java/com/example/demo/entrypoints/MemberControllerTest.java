package com.example.demo.entrypoints;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberStatus;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ControllerTest
class MemberControllerTest {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private MemberInMemoryRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @AfterEach
    void set() {
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

    @Nested
    class PutMember {
        @Test
        public void shouldUpdateMemberAndReturn200WhenMemberExists() {
            MemberId memberId = memberRepository.generateNewId();
            memberRepository.add(new Member(memberId, "Jean", "Dupond", "jean.d@smth.com", MemberStatus.NEW_MEMBER));

            HttpEntity<String> requestEntity = HttpEntityFactory.httpEntityFomJson("""
                    {
                        "id": "%s",
                        "firstName": "Paul",
                        "lastName": "Durant"
                    }
                    """.formatted(memberId.toValueString()));

            ResponseEntity<String> response = template.exchange("/member/" + memberId.toValueString(), HttpMethod.PUT, requestEntity, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            assertThat(memberRepository.countAll()).isEqualTo(1);

            MemberId id = MemberInMemoryRepository.MEMBER_IDS.getFirst();

            assertThat(memberRepository.findById(id)).hasValueSatisfying(member -> {
                Member expectedMember = new Member(id, "Paul", "Durant", "jean.d@smth.com", MemberStatus.NEW_MEMBER);
                assertThat(member).usingRecursiveComparison().isEqualTo(expectedMember);
            });
        }

        @Test
        public void shouldReturn404WhenMemberDoesNotExist() {
            HttpEntity<String> requestEntity = HttpEntityFactory.httpEntityFomJson("""
                    {
                        "id": "001b0068-1eb5-4c65-85c4-1b1eb788ecd5",
                        "firstName": "Paul",
                        "lastName": "Durant"
                    }
                    """);

            ResponseEntity<String> response = template.exchange("/member/001b0068-1eb5-4c65-85c4-1b1eb788ecd5", HttpMethod.PUT, requestEntity, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
