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

    private static final String ORDER_SERVICE_BASE_URL = "http://order-service:8080";

    private final BookRepository bookRepository;
    private final WebClient webClient;

    public BookService(BookRepository bookRepository, WebClient.Builder webClientBuilder) {
        this.bookRepository = bookRepository;
        this.webClient = webClientBuilder.baseUrl(ORDER_SERVICE_BASE_URL).build();
    }

    private BookDTO toDTO(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getPrice(), book.getStock());
    }

    private Book toEntity(BookDTO bookDTO) {
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
        validateBookData(bookDTO);
        Book book = toEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return toDTO(savedBook);
    }

    public Optional<BookDTO> updateBook(Long id, BookDTO bookDTO) {
        validateBookData(bookDTO);
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setStock(bookDTO.getStock());

        Book savedBook = bookRepository.save(existingBook);
        return Optional.of(toDTO(savedBook));
    }


    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found");
        }
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

        if (book.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        webClient.post()
                .uri("/orders/update-stock")
                .bodyValue(Map.of("bookId", bookId, "quantity", quantity))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(response -> {
                    book.setStock(book.getStock() - quantity);
                    bookRepository.save(book);
                })
                .block();
    }

    private void validateBookData(BookDTO bookDTO) {
        if (bookDTO.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (bookDTO.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        if (bookDTO.getTitle() == null || bookDTO.getTitle().length() < 3) {
            throw new IllegalArgumentException("Title must be at least 3 characters long");
        }
        if (bookDTO.getAuthor() == null || bookDTO.getAuthor().length() < 3) {
            throw new IllegalArgumentException("Author must be at least 3 characters long");
        }
    }
}
