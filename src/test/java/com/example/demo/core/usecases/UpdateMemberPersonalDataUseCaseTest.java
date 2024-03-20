package com.example.demo.core.usecases;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberRepository;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateMemberPersonalDataUseCaseTest {
    private UpdateMemberPersonalDataUseCase updateMemberPersonalDataUseCase;

    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberInMemoryRepository();
        updateMemberPersonalDataUseCase = new UpdateMemberPersonalDataUseCase(memberRepository);
    }

    @Test
    void shouldUpdatePersonalDataWhenMemberExists() {
        MemberId id = memberRepository.generateNewId();
        var member = Member.registerMember(id, "Jean", "Dupont", "jean.d@smth.com");
        memberRepository.add(member);

        var command = CommandForTest.builder().firstName("Paul").lastName("Durand").memberId(id.toValueString()).build();
        var result = updateMemberPersonalDataUseCase.execute(command, new PresenterForTest());
        assertThat(result).isEqualTo("success");

        var updatedMember = memberRepository.findById(id);
        var expectedMember = Member.registerMember(id, "Paul", "Durand", "jean.d@smth.com");

        assertThat(updatedMember.get()).usingRecursiveComparison().isEqualTo(expectedMember);
    }

    @Test
    void shouldThrowWhenMemberDoesNotExist() {
        MemberId id = memberRepository.generateNewId();

        var command = CommandForTest.builder().firstName("Paul").lastName("Durand").memberId(id.toValueString()).build();
        assertThatThrownBy(() -> updateMemberPersonalDataUseCase.execute(command, new PresenterForTest())).hasMessage("member does not exist");
    }

    @Getter
    @Builder
    static class CommandForTest implements UpdateMemberPersonalDataUseCase.UpdateMemberPersonalDataCommand {
        public String memberId;
        public String firstName;
        public String lastName;
    }

    static class PresenterForTest implements UpdateMemberPersonalDataUseCase.UpdateMemberPersonalDataPresenter<String> {

        @Override
        public String presentSuccess() {
            return "success";
        }

        @Override
        public String presentMemberDoesNotExist() {
            throw new RuntimeException("member does not exist");
        }
    }
}
