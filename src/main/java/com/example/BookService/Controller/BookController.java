package com.example.BookService.Controller;

import com.example.BookService.DTO.BookDTO;
import com.example.BookService.Service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.ok(createdBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        return bookService.updateBook(id, bookDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> findBooksByAuthorAndTitle(@RequestParam String author, @RequestParam String title) {
        List<BookDTO> books = bookService.findBooksByAuthorAndTitle(author, title);
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<String> reduceStock(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            bookService.reduceBookStock(id, quantity);
            return ResponseEntity.ok("Stock reduced successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<Integer> getStockById(@PathVariable Long id) {
        Optional<BookDTO> bookDTO = bookService.getBookById(id);
        if (bookDTO.isPresent()) {
            return ResponseEntity.ok(bookDTO.get().getStock());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
