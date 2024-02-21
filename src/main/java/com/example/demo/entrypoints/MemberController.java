package com.example.demo.entrypoints;

import com.example.demo.core.domain.Member;
import com.example.demo.core.usecases.AddMemberUseCase;
import org.apache.coyote.BadRequestException;
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
        public AddMemberResponseBodyDTO presentCreatedMember(Member createdMember) {
            AddMemberResponseBodyDTO responseBodyDTO = new AddMemberResponseBodyDTO();

            createdMember.provideInterest(new Member.MemberInterest() {
                @Override
                public void informId(String id) {
                    responseBodyDTO.id = id;
                }

                @Override
                public void informFirstName(String firstName) {
                    responseBodyDTO.firstName = firstName;
                }

                @Override
                public void informLastName(String lastName) {
                    responseBodyDTO.lastName = lastName;
                }

                @Override
                public void informEmail(String email) {
                    responseBodyDTO.email = email;
                }
            });

            return responseBodyDTO;
        }

        @Override
        public AddMemberResponseBodyDTO presentAnotherMemberExistsWithSameEmail() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Another member with same email exists");
        }
    }
}
