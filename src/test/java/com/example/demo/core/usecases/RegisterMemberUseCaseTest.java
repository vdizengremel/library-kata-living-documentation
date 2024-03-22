package com.example.demo.core.usecases;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberRepository;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RegisterMemberUseCaseTest {
    private RegisterMemberUseCase registerMemberUseCase;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberInMemoryRepository();
        registerMemberUseCase = new RegisterMemberUseCase(memberRepository);
    }

    @Test
    void shouldPresentCreatedMember_WhenMemberDoesNotExist() {
        AddMemberUseCaseCommandForTest addMemberUseCaseCommand = AddMemberUseCaseCommandForTest.builder().firstName("firstname").build();
        Member result = registerMemberUseCase.execute(addMemberUseCaseCommand, new AddMemberUseCasePresenterForTest());

        Member member = getFirstCreatedMember();
        assertThat(result).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    void shouldAddMemberWithCorrectInformation_WhenMemberDoesNotExist() {
        AddMemberUseCaseCommandForTest addMemberUseCaseCommand = AddMemberUseCaseCommandForTest.builder().firstName("Jean").lastName("Dupond").email("jean.dupond@any.com").build();
        registerMemberUseCase.execute(addMemberUseCaseCommand, new AddMemberUseCasePresenterForTest());

        Member actual = getFirstCreatedMember();
        assertThat(actual).usingRecursiveComparison().isEqualTo(Member.registerMember(MemberInMemoryRepository.MEMBER_IDS.getFirst(), "Jean", "Dupond", "jean.dupond@any.com"));
    }

    @Test
    void shouldPresentMemberAlreadyExistsMessage_WhenMemberAlreadyExistsWithSameEmail() {
        var builder = AddMemberUseCaseCommandForTest.builder().firstName("Jean").lastName("Dupond").email("jean.dupond@any.com");
        var firstAddMemberUseCaseCommandToCreateMember = builder.build();
        var secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail = builder.build();

        registerMemberUseCase.execute(firstAddMemberUseCaseCommandToCreateMember, new AddMemberUseCasePresenterForTest());
        assertThatThrownBy(() -> registerMemberUseCase.execute(secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail, new AddMemberUseCasePresenterForTest()))
                .hasMessage("AnotherMemberExistsWithSameEmail");
    }

    @Test
    void shouldNotChangeMember_WhenMemberAlreadyExistsWithSameEmail() {
        var builder = AddMemberUseCaseCommandForTest.builder().firstName("Jean").lastName("Dupond").email("jean.dupond@any.com");
        var firstAddMemberUseCaseCommandToCreateMember = builder.build();

        Member addedResult = registerMemberUseCase.execute(firstAddMemberUseCaseCommandToCreateMember, new AddMemberUseCasePresenterForTest());

        var secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail = builder.firstName("Paul").build();
        assertThatThrownBy(() -> registerMemberUseCase.execute(secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail, new AddMemberUseCasePresenterForTest()));

        var count = memberRepository.countAll();
        assertThat(count).isEqualTo(1);

        Member member = getFirstCreatedMember();
        assertThat(addedResult).usingRecursiveComparison().isEqualTo(member);
    }

    @NotNull
    private Member getFirstCreatedMember() {
        var createdMember = memberRepository.findById(MemberInMemoryRepository.MEMBER_IDS.getFirst());
        return createdMember.orElseThrow(() -> new RuntimeException("No created member"));
    }

    @Getter
    @Builder
    static class AddMemberUseCaseCommandForTest implements RegisterMemberUseCase.AddMemberUseCaseCommand {
        private String firstName;
        private String lastName;
        private String email;
    }

    static class AddMemberUseCasePresenterForTest implements RegisterMemberUseCase.AddMemberUseCasePresenter<Member> {

        @Override
        public Member presentAddedMember(Member addedMember) {
            return addedMember;
        }

        @Override
        public Member presentErrorAnotherMemberExistsWithSameEmail() {
            throw new RuntimeException("AnotherMemberExistsWithSameEmail");
        }
    }
}
