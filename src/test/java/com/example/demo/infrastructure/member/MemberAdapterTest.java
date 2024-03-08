package com.example.demo.infrastructure.member;

import com.example.demo.ProjectMongoContainer;
import com.example.demo.core.domain.member.MemberRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
@ActiveProfiles("repository-test")
public class MemberAdapterTest extends AbstractMemberRepositoryTest<MemberRepository> {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberMongoSpringRepository memberMongoSpringRepository;

    @ClassRule
    public static MongoDBContainer mongoDBContainer = ProjectMongoContainer.getInstance();

    @BeforeAll
    static void beforeAll() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mongoDBContainer.stop();
    }

    @Override
    MemberRepository getMemberRepository() {
        return memberRepository;
    }

    @Override
    public void deleteAll() {
        memberMongoSpringRepository.deleteAll();
    }
}
