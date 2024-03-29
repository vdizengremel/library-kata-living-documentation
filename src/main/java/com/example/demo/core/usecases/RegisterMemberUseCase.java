package com.example.demo.core.usecases;

import com.example.living.documentation.annotation.UseCase;
import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberRepository;

@UseCase
public class RegisterMemberUseCase {
    private final MemberRepository memberRepository;

    public RegisterMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public <T> T execute(AddMemberUseCaseCommand command, AddMemberUseCasePresenter<T> presenter) {
        if (memberRepository.existsWithEmail(command.getEmail())) {
            return presenter.presentErrorAnotherMemberExistsWithSameEmail();
        }

        var member = Member.registerMember(
                memberRepository.generateNewId(),
                command.getFirstName(),
                command.getLastName(),
                command.getEmail()
        );

        memberRepository.add(member);
        return presenter.presentAddedMember(member);
    }

    public interface AddMemberUseCaseCommand {
        String getFirstName();

        String getLastName();

        String getEmail();
    }

    public interface AddMemberUseCasePresenter<T> {
        T presentAddedMember(Member addedMember);

        T presentErrorAnotherMemberExistsWithSameEmail();
    }
}
