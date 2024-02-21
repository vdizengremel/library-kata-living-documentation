package com.example.demo.entrypoints;

import com.example.demo.core.usecases.AddMemberUseCase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMemberRequestBodyDTO implements AddMemberUseCase.AddMemberUseCaseCommand {
    private String firstName;
    private String lastName;
    private String email;
}
