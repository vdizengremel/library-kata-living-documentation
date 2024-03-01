package com.example.demo.core.usecases;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;
import com.example.demo.infrastructure.MemberInMemoryRepository;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AddMemberUseCaseTest {
    private AddMemberUseCase addMemberUseCase;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberInMemoryRepository();
        addMemberUseCase = new AddMemberUseCase(memberRepository);
    }

    @Test
    void shouldPresentCreatedMember_WhenMemberDoesNotExist() {
        AddMemberUseCaseCommandForTest addMemberUseCaseCommand = AddMemberUseCaseCommandForTest.builder().firstName("firstname").build();
        Member result = addMemberUseCase.execute(addMemberUseCaseCommand, new AddMemberUseCasePresenterForTest());

        Member member = getFirstCreatedMember();
        assertThat(result).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    void shouldAddMemberWithCorrectInformation_WhenMemberDoesNotExist() {
        AddMemberUseCaseCommandForTest addMemberUseCaseCommand = AddMemberUseCaseCommandForTest.builder().firstName("Jean").lastName("Dupond").email("jean.dupond@any.com").build();
        addMemberUseCase.execute(addMemberUseCaseCommand, new AddMemberUseCasePresenterForTest());

        Member actual = getFirstCreatedMember();
        assertThat(actual).usingRecursiveComparison().isEqualTo(new Member(new MemberId(MemberInMemoryRepository.UUIDS.getFirst()), "Jean", "Dupond", "jean.dupond@any.com", 5));
    }

    @Test
    void shouldPresentMemberAlreadyExistsMessage_WhenMemberAlreadyExistsWithSameEmail() {
        var builder = AddMemberUseCaseCommandForTest.builder().firstName("Jean").lastName("Dupond").email("jean.dupond@any.com");
        var firstAddMemberUseCaseCommandToCreateMember = builder.build();
        var secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail = builder.build();

        addMemberUseCase.execute(firstAddMemberUseCaseCommandToCreateMember, new AddMemberUseCasePresenterForTest());
        assertThatThrownBy(() -> addMemberUseCase.execute(secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail, new AddMemberUseCasePresenterForTest()))
                .hasMessage("AnotherMemberExistsWithSameEmail");
    }

    @Test
    void shouldNotChangeMember_WhenMemberAlreadyExistsWithSameEmail() {
        var builder = AddMemberUseCaseCommandForTest.builder().firstName("Jean").lastName("Dupond").email("jean.dupond@any.com");
        var firstAddMemberUseCaseCommandToCreateMember = builder.build();

        Member addedResult = addMemberUseCase.execute(firstAddMemberUseCaseCommandToCreateMember, new AddMemberUseCasePresenterForTest());

        var secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail = builder.firstName("Paul").build();
        assertThatThrownBy(() -> addMemberUseCase.execute(secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail, new AddMemberUseCasePresenterForTest()));

        var count = memberRepository.countAll();
        assertThat(count).isEqualTo(1);

        Member member = getFirstCreatedMember();
        assertThat(addedResult).usingRecursiveComparison().isEqualTo(member);
    }

    @NotNull
    private Member getFirstCreatedMember() {
        var createdMember = memberRepository.findById(new MemberId(MemberInMemoryRepository.UUIDS.getFirst()));
        return createdMember.orElseThrow(() -> new RuntimeException("No created member"));
    }

    @Getter
    @Builder
    static class AddMemberUseCaseCommandForTest implements AddMemberUseCase.AddMemberUseCaseCommand {
        private String firstName;
        private String lastName;
        private String email;
    }

    static class AddMemberUseCasePresenterForTest implements AddMemberUseCase.AddMemberUseCasePresenter<Member> {

        @Override
        public Member presentCreatedMember(Member createdMember) {
            return createdMember;
        }

        @Override
        public Member presentAnotherMemberExistsWithSameEmail() {
            throw new RuntimeException("AnotherMemberExistsWithSameEmail", new Throwable());
        }
    }
}
