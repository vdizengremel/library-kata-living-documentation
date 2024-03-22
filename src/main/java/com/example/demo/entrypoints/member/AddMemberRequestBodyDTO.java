package com.example.demo.entrypoints.member;

import com.example.demo.core.usecases.RegisterMemberUseCase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMemberRequestBodyDTO implements RegisterMemberUseCase.AddMemberUseCaseCommand {
    private String firstName;
    private String lastName;
    private String email;
}
