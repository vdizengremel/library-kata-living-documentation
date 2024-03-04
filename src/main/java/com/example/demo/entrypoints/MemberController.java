package com.example.demo.entrypoints;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.usecases.AddMemberUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("member")
public class MemberController {
    private final AddMemberUseCase addMemberUseCase;

    public MemberController(AddMemberUseCase addMemberUseCase) {
        this.addMemberUseCase = addMemberUseCase;
    }

    @PostMapping("/")
    public AddMemberResponseBodyDTO addMember(@RequestBody AddMemberRequestBodyDTO addMemberRequestBodyDTO) {
        return addMemberUseCase.execute(addMemberRequestBodyDTO, new AddMemberUseCaseHttpPresenter());
    }

    static class AddMemberUseCaseHttpPresenter implements AddMemberUseCase.AddMemberUseCasePresenter<AddMemberResponseBodyDTO> {

        @Override
        public AddMemberResponseBodyDTO presentAddedMember(Member addedMember) {
            AddMemberResponseBodyDTO responseBodyDTO = new AddMemberResponseBodyDTO();
            addedMember.provideInterest(responseBodyDTO);
            return responseBodyDTO;
        }

        @Override
        public AddMemberResponseBodyDTO presentErrorAnotherMemberExistsWithSameEmail() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Another member with same email exists");
        }
    }
}
