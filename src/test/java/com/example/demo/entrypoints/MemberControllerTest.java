package com.example.demo.entrypoints;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;
import com.example.demo.infrastructure.UUIDGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"inMemoryRepository"})
@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@ComponentScan(basePackages = "com.example.demo")
class MemberControllerTest {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private UUIDGenerator uuidGenerator;

    @BeforeEach
    void setUp() {
        when(uuidGenerator.generateUUID()).thenReturn(UUID.fromString("001b0068-1eb5-4c65-85c4-1b1eb788ecd5"));
    }

    @Test
    public void shouldAddMemberAndReturn200() {
        AddMemberRequestBodyDTO addMemberRequestBodyDTO = new AddMemberRequestBodyDTO();
        addMemberRequestBodyDTO.setFirstName("Jean");
        addMemberRequestBodyDTO.setLastName("Dupond");
        addMemberRequestBodyDTO.setEmail("jean.dupond@somemail.com");

        ResponseEntity<AddMemberResponseBodyDTO> response = template.postForEntity("/member/", addMemberRequestBodyDTO, AddMemberResponseBodyDTO.class);

        AddMemberResponseBodyDTO expected = new AddMemberResponseBodyDTO();
        expected.id = "001b0068-1eb5-4c65-85c4-1b1eb788ecd5";
        expected.firstName = "Jean";
        expected.lastName = "Dupond";
        expected.email = "jean.dupond@somemail.com";

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expected);

        assertThat(memberRepository.countAll()).isEqualTo(1);
        MemberId id = new MemberId(UUID.fromString("001b0068-1eb5-4c65-85c4-1b1eb788ecd5"));
        assertThat(memberRepository.findById(id).get()).usingRecursiveComparison().isEqualTo(new Member(id, "Jean", "Dupond", "jean.dupond@somemail.com", 5));
        assertThat(memberRepository.findById(id)).hasValueSatisfying(member -> {
            assertThat(member).usingRecursiveComparison().isEqualTo(new Member(id, "Jean", "Dupond", "jean.dupond@somemail.com", 5));
        });
    }

    @Test
    public void shouldNotAddMemberAndReturn400WhenAlreadyExistsWithSameEmail() {
        AddMemberRequestBodyDTO addMemberRequestBodyDTO = new AddMemberRequestBodyDTO();
        addMemberRequestBodyDTO.setFirstName("Jean");
        addMemberRequestBodyDTO.setLastName("Dupond");
        addMemberRequestBodyDTO.setEmail("jean.dupond@somemail.com");

        template.postForEntity("/member/", addMemberRequestBodyDTO, AddMemberResponseBodyDTO.class);
        ResponseEntity<AddMemberResponseBodyDTO> response = template.postForEntity("/member/", addMemberRequestBodyDTO, AddMemberResponseBodyDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(memberRepository.countAll()).isEqualTo(1);
    }
}
