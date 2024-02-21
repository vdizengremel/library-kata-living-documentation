package com.example.demo.entrypoints;

import com.example.demo.ProjectMongoContainer;
import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.infrastructure.MemberMongoSpringRepository;
import com.example.demo.infrastructure.UUIDGenerator;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"tc", "tc-auto"})
class MemberControllerTest {
    @Autowired
    private TestRestTemplate template;

    @ClassRule
    public static MongoDBContainer mongoDBContainer = ProjectMongoContainer.getInstance();

    @Autowired
    private MemberMongoSpringRepository memberMongoSpringRepository;

    @MockBean
    private UUIDGenerator uuidGenerator;

    @BeforeAll
    static void beforeAll() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mongoDBContainer.stop();
    }

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

        List<Member> allMembers = memberMongoSpringRepository.findAll();
        assertThat(allMembers).hasSize(1);
        assertThat(allMembers.get(0)).usingRecursiveComparison().isEqualTo(new Member(new MemberId(UUID.fromString("001b0068-1eb5-4c65-85c4-1b1eb788ecd5")), "Jean", "Dupond", "jean.dupond@somemail.com", 5));
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

        List<Member> allMembers = memberMongoSpringRepository.findAll();
        assertThat(allMembers).hasSize(1);
    }
}
