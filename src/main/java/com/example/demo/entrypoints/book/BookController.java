package com.example.demo.entrypoints.book;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.usecases.GetBookByIsbnUseCase;
import com.example.demo.core.usecases.RegisterABookUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("book")
public class BookController {
    private final RegisterABookUseCase registerABookUseCase;
    private final GetBookByIsbnUseCase getBookByIsbnUseCase;

    public BookController(RegisterABookUseCase registerABookUseCase, GetBookByIsbnUseCase getBookByIsbnUseCase) {
        this.registerABookUseCase = registerABookUseCase;
        this.getBookByIsbnUseCase = getBookByIsbnUseCase;
    }

    @PostMapping("/")
    public ResponseEntity<Void> addMember(@RequestBody BookHttpDTO bookHttpDTO) {
       return registerABookUseCase.execute(bookHttpDTO, new RegisterABookUseCase.RegisterABookPresenter<>() {
           @Override
           public ResponseEntity<Void> presentRegistrationSuccess() {
               return ResponseEntity.ok(null);
           }

           @Override
           public ResponseEntity<Void> presentBookAlreadyRegistered() {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
           }
       });
    }

    @GetMapping("/{id}")
    public BookHttpDTO getById(@PathVariable("id") String bookId) {
        return getBookByIsbnUseCase.execute(() -> bookId, new GetBookByIsbnUseCase.GetBookByIsbnPresenter<>() {
            @Override
            public BookHttpDTO presentBook(Book book) {
                BookHttpDTO bookHttpDTO = new BookHttpDTO();
                book.provideInterest(bookHttpDTO);
                return bookHttpDTO;
            }

            @Override
            public BookHttpDTO presentBookNotFound() {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        });
    }
}
