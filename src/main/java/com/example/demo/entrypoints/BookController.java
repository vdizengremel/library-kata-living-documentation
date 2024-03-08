package com.example.demo.entrypoints;

import com.example.demo.core.usecases.RegisterABookUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("book")
public class BookController {
    private final RegisterABookUseCase registerABookUseCase;

    public BookController(RegisterABookUseCase registerABookUseCase) {
        this.registerABookUseCase = registerABookUseCase;
    }

    @PostMapping("/")
    public ResponseEntity<Void> addMember(@RequestBody BookDTO bookDTO) {
       return registerABookUseCase.execute(bookDTO, new RegisterABookUseCase.RegisterABookPresenter<ResponseEntity<Void>>() {
           @Override
           public ResponseEntity<Void> presentRegistrationSuccess() {
               return ResponseEntity.ok(null);
           }

           @Override
           public ResponseEntity<Void> presentBoolAlreadyRegistered() {
               return null;
           }
       });
    }
}
