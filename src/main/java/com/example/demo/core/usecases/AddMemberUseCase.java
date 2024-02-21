package com.example.demo.core.usecases;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddMemberUseCase {
    private final MemberRepository memberRepository;

    public AddMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public <T> T execute(AddMemberUseCaseCommand command, AddMemberUseCasePresenter<T> presenter) {
        if(memberRepository.existsWithEmail(command.getEmail())) {
            return presenter.presentAnotherMemberExistsWithSameEmail();
        }

        var member = new Member(
                memberRepository.generateNewId(),
                command.getFirstName(),
                command.getLastName(),
                command.getEmail(),
                5
        );

        memberRepository.add(member);
        return presenter.presentCreatedMember(member);
    }

   public interface AddMemberUseCaseCommand {
        String getFirstName();

        String getLastName();

        String getEmail();
    }

    public interface AddMemberUseCasePresenter<T> {
        T presentCreatedMember(Member createdMember);
        T presentAnotherMemberExistsWithSameEmail();
    }
}
