package com.example.bookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class BookRequestDto {
    @NotBlank(message = "should not be blank")
    private String title;
    @NotBlank(message = "should not be blank")
    private String author;
    @NotBlank(message = "should not be blank")
    @Size(min = 13, message = "should contain 13-digits")
    private String isbn;
    @NotNull(message = "should not be null")
    @Positive(message = "should be greater than 0")
    private BigDecimal price;
    @NotBlank(message = "should be not blank")
    private String description;
    @NotBlank(message = "should be not blank")
    @URL(message = "invalid. Please provide a valid URL")
    private String coverImage;
}
