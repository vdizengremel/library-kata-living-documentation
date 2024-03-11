package com.example.demo.entrypoints.member;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberStatus;
import lombok.Setter;

@Setter
public class MemberHttpDTO implements Member.MemberInterest {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public MemberStatus status;

}
