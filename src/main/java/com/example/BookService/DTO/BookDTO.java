package com.example.BookService.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    @NotNull
    @Size(min = 3, message = "Title must be at least 3 characters long")
    private String title;
    @NotNull
    @Size(min = 3, message = "Author must be at least 3 characters long")
    private String author;
    @NotNull
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;
    @NotNull
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
