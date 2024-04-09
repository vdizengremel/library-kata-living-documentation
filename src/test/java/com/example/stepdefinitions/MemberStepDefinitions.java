package com.example.stepdefinitions;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberStatus;
import com.example.demo.core.usecases.RegisterMemberUseCase;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import com.example.test.MemberPersonas;
import com.example.test.PresenterException;
import com.example.test.World;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.test.CucumberUtils.catchPresenterException;
import static org.assertj.core.api.Assertions.assertThat;

public class MemberStepDefinitions {
    private final MemberInMemoryRepository memberInMemoryRepository;
    private final RegisterMemberUseCase registerMemberUseCase;
    private final MemberPersonas memberPersonas;
    private PresenterException thrownException;
    private MemberId lastMemberId;


    public MemberStepDefinitions(World world) {
        memberInMemoryRepository = world.memberInMemoryRepository;
        memberPersonas = world.memberPersonas;
        this.registerMemberUseCase = new RegisterMemberUseCase(memberInMemoryRepository);
    }

    @Given("{} is a registered member")
    public void followingMemberExist(String name) {
        AddMemberUseCaseCommandForTest command = memberPersonas.findByName(name);
        Member registeredMember = registerMemberUseCase.execute(command, new AddMemberUseCasePresenterForTest());
        memberPersonas.keepIdFor(name, registeredMember.getId().toValueString());
    }

    @When("a person registers with information:")
    public void aPersonRegistersWithInformation(List<Map<String, String>> personInformation) {
        var person = personInformation.getFirst();
        thrownException = catchPresenterException(() -> registerMember(person));
    }

    @Then("last registered member should be:")
    public void lastMemberShouldBeRegisteredWith(List<Map<String, String>> registeredMembersData) {
        var memberData = registeredMembersData.getFirst();
        var expectedMember = new Member(lastMemberId, memberData.get("firstname"), memberData.get("lastname"), memberData.get("email"), MemberStatus.NEW_MEMBER);

        Optional<Member> optionalMember = memberInMemoryRepository.findById(lastMemberId);
        assertThat(optionalMember).isPresent();
        assertThat(optionalMember.get()).usingRecursiveComparison().isEqualTo(expectedMember);
    }

    @Then("the member registration should fail because {}")
    public void theResultShouldBeAnErrorIndicatingAMemberWithSameEmailExists(String expectingMessage) {
        assertThat(thrownException).hasMessage(expectingMessage);
    }

    @Then("{} should have not changed")
    public void mattShouldHaveNotChanged(String memberName) {
        MemberId memberId = MemberId.from(memberPersonas.getIdFor(memberName));
        Optional<Member> optionalMember = memberInMemoryRepository.findById(memberId);
        assertThat(optionalMember).isPresent();

        AddMemberUseCaseCommandForTest command = memberPersonas.findByName(memberName);
        Member expectedMember = new Member(memberId, command.firstName, command.lastName, command.email, MemberStatus.NEW_MEMBER);
        assertThat(optionalMember.get()).usingRecursiveComparison().isEqualTo(expectedMember);
    }

    private void registerMember(Map<String, String> existingMember) {
        var command = AddMemberUseCaseCommandForTest.builder()
                .firstName(existingMember.get("firstname"))
                .lastName(existingMember.get("lastname"))
                .email(existingMember.get("email"))
                .build();

        Optional.ofNullable(existingMember.get("id")).map(MemberId::from).ifPresent(memberInMemoryRepository::setNextGeneratedId);

        var registeredMember = registerMemberUseCase.execute(command, new AddMemberUseCasePresenterForTest());
        lastMemberId = registeredMember.getId();
    }

    @Getter
    @Builder
    public static class AddMemberUseCaseCommandForTest implements RegisterMemberUseCase.AddMemberUseCaseCommand {
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
}
