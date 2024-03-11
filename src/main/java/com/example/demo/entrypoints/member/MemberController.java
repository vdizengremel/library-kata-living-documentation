package com.example.demo.entrypoints.member;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.usecases.AddMemberUseCase;
import com.example.demo.core.usecases.UpdateMemberPersonalDataUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("member")
public class MemberController {
    private final AddMemberUseCase addMemberUseCase;
    private final UpdateMemberPersonalDataUseCase updateMemberPersonalDataUseCase;

    public MemberController(AddMemberUseCase addMemberUseCase, UpdateMemberPersonalDataUseCase updateMemberPersonalDataUseCase) {
        this.addMemberUseCase = addMemberUseCase;
        this.updateMemberPersonalDataUseCase = updateMemberPersonalDataUseCase;
    }

    @PostMapping("/")
    public MemberHttpDTO addMember(@RequestBody AddMemberRequestBodyDTO addMemberRequestBodyDTO) {
        return addMemberUseCase.execute(addMemberRequestBodyDTO, new AddMemberUseCaseHttpPresenter());
    }

    @PutMapping("/{id}")
    public void updateMember(@PathVariable("id") String memberId, @RequestBody UpdateMemberHttpDTO updateMemberHttpDTO) {
        this.updateMemberPersonalDataUseCase.execute(toUseCaseCommand(memberId, updateMemberHttpDTO), new UpdateMemberPersonalDataUseCase.UpdateMemberPersonalDataPresenter<Void>() {

            @Override
            public Void presentSuccess() {
                return null;
            }

            @Override
            public Void presentMemberDoesNotExist() {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        });
    }

    private static UpdateMemberPersonalDataUseCase.UpdateMemberPersonalDataCommand toUseCaseCommand(String memberId, UpdateMemberHttpDTO updateMemberHttpDTO) {
        return new UpdateMemberPersonalDataUseCase.UpdateMemberPersonalDataCommand() {

            @Override
            public String getMemberId() {
                return memberId;
            }

            @Override
            public String getFirstName() {
                return updateMemberHttpDTO.getFirstName();
            }

            @Override
            public String getLastName() {
                return updateMemberHttpDTO.getLastName();
            }
        };
    }

    static class AddMemberUseCaseHttpPresenter implements AddMemberUseCase.AddMemberUseCasePresenter<MemberHttpDTO> {

        @Override
        public MemberHttpDTO presentAddedMember(Member addedMember) {
            MemberHttpDTO responseBodyDTO = new MemberHttpDTO();
            addedMember.provideInterest(responseBodyDTO);
            return responseBodyDTO;
        }

        @Override
        public MemberHttpDTO presentErrorAnotherMemberExistsWithSameEmail() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Another member with same email exists");
        }
    }
}
