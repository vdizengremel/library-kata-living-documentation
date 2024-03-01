package com.example.demo.core.usecases;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;
import com.example.demo.infrastructure.MemberInMemoryRepository;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        String result = addMemberUseCase.execute(addMemberUseCaseCommand, new AddMemberUseCasePresenterForTest());

        Member member = getFirstCreatedMember();
        assertThat(result).isEqualTo(member.toString());
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
        String result = addMemberUseCase.execute(secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail, new AddMemberUseCasePresenterForTest());

        assertThat(result).isEqualTo("AnotherMemberExistsWithSameEmail");
    }

    @Test
    void shouldNotChangeMember_WhenMemberAlreadyExistsWithSameEmail() {
        var builder = AddMemberUseCaseCommandForTest.builder().firstName("Jean").lastName("Dupond").email("jean.dupond@any.com");
        var firstAddMemberUseCaseCommandToCreateMember = builder.build();

        String addedResult = addMemberUseCase.execute(firstAddMemberUseCaseCommandToCreateMember, new AddMemberUseCasePresenterForTest());

        var secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail = builder.firstName("Paul").build();
        addMemberUseCase.execute(secondAddMemberUseCaseCommandToTryAddingSameMemberWithSameEmail, new AddMemberUseCasePresenterForTest());

        var count = memberRepository.countAll();
        assertThat(count).isEqualTo(1);

        Member member = getFirstCreatedMember();
        assertThat(addedResult).isEqualTo(member.toString());
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

    static class AddMemberUseCasePresenterForTest implements AddMemberUseCase.AddMemberUseCasePresenter<String> {

        @Override
        public String presentCreatedMember(Member createdMember) {
            return createdMember.toString();
        }

        @Override
        public String presentAnotherMemberExistsWithSameEmail() {
            return "AnotherMemberExistsWithSameEmail";
        }
    }
}
