package com.example.demo.core.usecases;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class AddMemberUseCase {
    private final MemberRepository memberRepository;

    public AddMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public <T> T execute(AddMemberUseCaseCommand command, AddMemberUseCasePresenter<T> presenter) {
        if (memberRepository.existsWithEmail(command.getEmail())) {
            return presenter.presentErrorAnotherMemberExistsWithSameEmail();
        }

        var member = Member.createNewMember(
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
