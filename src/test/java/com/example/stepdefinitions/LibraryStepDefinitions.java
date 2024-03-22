package com.example.stepdefinitions;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberStatus;
import com.example.demo.core.usecases.RegisterMemberUseCase;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LibraryStepDefinitions {
    private MemberInMemoryRepository memberInMemoryRepository;

    private PresenterException thrownException;
    private Member lastRegisteredMember;


    @Given("already registered members:")
    public void followingMembersExist(List<Map<String, String>> existingMembers) {
        existingMembers.forEach(this::registerMember);
    }

    @When("a person registers with information:")
    public void aPersonRegistersWithInformation(List<Map<String, String>> personInformation) {
        var person = personInformation.getFirst();

        try {
           registerMember(person);
        } catch (PresenterException presenterException) {
            thrownException = presenterException;
        }
    }

    @Then("a registered member should be:")
    public void aMemberShouldBeRegisteredWith(List<Map<String, String>> registeredMember) {
        var member = registeredMember.getFirst();
        MemberId lastGeneratedId = MemberId.from(member.get("id"));
        Member expectedMember = new Member(lastGeneratedId, member.get("firstname"), member.get("lastname"), member.get("email"), MemberStatus.NEW_MEMBER);

        assertThat(lastRegisteredMember).usingRecursiveComparison().isEqualTo(expectedMember);

        Optional<Member> optionalMember = memberInMemoryRepository.findById(lastGeneratedId);
        assertThat(optionalMember).isPresent();
        assertThat(optionalMember.get()).usingRecursiveComparison().isEqualTo(expectedMember);
    }

    private void registerMember(Map<String, String> existingMember) {
        var command = AddMemberUseCaseCommandForTest.builder()
                .firstName(existingMember.get("firstname"))
                .lastName(existingMember.get("lastname"))
                .email(existingMember.get("email"))
                .build();

        var registerMemberUseCase = new RegisterMemberUseCase(memberInMemoryRepository);
        this.lastRegisteredMember = registerMemberUseCase.execute(command, new AddMemberUseCasePresenterForTest());
    }

    @Then("the result should be an error indicating {}")
    public void theResultShouldBeAnErrorIndicatingAMemberWithSameEmailExists(String expectingMessage) {
        assertThat(thrownException).hasMessage(expectingMessage);
    }

    @Given("next generated member ids will be:")
    public void nextGeneratedMemberIdsWillBe(List<String> uuids) {
        var memberIds = uuids.stream().map(MemberId::from).toList();
        memberInMemoryRepository = new MemberInMemoryRepository(memberIds);
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
            throw new PresenterException("a member with same email exists");
        }
    }

    static class PresenterException extends RuntimeException {
        public PresenterException(String message) {
            super(message);
        }
    }
}
