package com.example.BookService.Service;

import com.example.BookService.DTO.BookDTO;
import com.example.BookService.Entity.Book;
import com.example.BookService.Repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final WebClient webClient;
    public BookService(BookRepository bookRepository, WebClient.Builder webClientBuilder) {
        this.bookRepository = bookRepository;
        this.webClient=webClientBuilder.baseUrl("http://order-service:8080").build();
    }

    private BookDTO toDTO(Book book){
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getPrice(), book.getStock());
    }

    private Book toEntity(BookDTO bookDTO){
        return new Book(bookDTO.getId(), bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getPrice(), bookDTO.getStock());
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id).map(this::toDTO);
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = toEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return toDTO(savedBook);
    }

    public Optional<BookDTO> updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id).map(existingBook -> {
            existingBook.setTitle(bookDTO.getTitle());
            existingBook.setAuthor(bookDTO.getAuthor());
            existingBook.setPrice(bookDTO.getPrice());
            existingBook.setStock(bookDTO.getStock());
            return bookRepository.save(existingBook);
        }).map(this::toDTO);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<BookDTO> findBooksByAuthorAndTitle(String author, String title) {
        return bookRepository.findByAuthorAndTitle(author, title).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public void reduceBookStock(Long bookId, Integer quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (book.getStock() >= quantity) {
            book.setStock(book.getStock() - quantity);
            bookRepository.save(book);

            webClient.post()
                    .uri("http://order-service/orders/update-stock")
                    .bodyValue(Map.of("bookId", bookId, "quantity", quantity))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } else {
            throw new IllegalArgumentException("Not enough stock available");
        }
    }
}
