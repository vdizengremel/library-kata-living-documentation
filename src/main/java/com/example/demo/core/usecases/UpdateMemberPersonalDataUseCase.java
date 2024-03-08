package com.example.demo.core.usecases;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateMemberPersonalDataUseCase {
    private final MemberRepository memberRepository;

    public UpdateMemberPersonalDataUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public <T> T execute(UpdateMemberPersonalDataCommand command, UpdateMemberPersonalDataPresenter<T> presenter) {
        var optionalMember = memberRepository.findById(MemberId.from(command.getMemberId()));

        if(optionalMember.isEmpty()) {
            return presenter.presentMemberDoesNotExist();
        }

        Member member = optionalMember.get();


        member.setFirstName(command.getFirstName());
        member.setLastName(command.getLastName());

        memberRepository.update(member);
        return presenter.presentSuccess();
    }

    public interface UpdateMemberPersonalDataCommand {
        String getMemberId();
        String getFirstName();
        String getLastName();
    }

    public interface UpdateMemberPersonalDataPresenter<T> {

        T presentSuccess();

        T presentMemberDoesNotExist();
    }
}
