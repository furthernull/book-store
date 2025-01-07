package com.example.bookstore.controller;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/add-default-books-to-db.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all.sql")
            );
        }
    }

    @Test
    @DisplayName("Create a new book")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createBook_ValidRequestDto_Success() throws Exception {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setTitle("Test");
        requestDto.setAuthor("Test");
        requestDto.setIsbn("1111111111-111");
        requestDto.setPrice(BigDecimal.valueOf(99.99));
        requestDto.setDescription("Test");
        requestDto.setCoverImage("https://example.com/test");
        requestDto.setCategoryIds(Set.of(1L));

        BookDto expected = new BookDto();
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setDescription(requestDto.getDescription());
        expected.setCoverImage(requestDto.getCoverImage());
        expected.setCategoryIds(requestDto.getCategoryIds());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Get all books")
    @WithMockUser(username = "user", authorities = {"USER"})
    void getAllBooks_ShouldReturnAllBooks() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto()
                .setId(1L)
                .setTitle("Book")
                .setAuthor("Author")
                .setIsbn("1234567890-123")
                .setPrice(BigDecimal.valueOf(19.99))
                .setDescription("description")
                .setCoverImage("https://example.com/image")
                .setCategoryIds(Set.of(1L)));
        expected.add(new BookDto()
                .setId(2L)
                .setTitle("Another")
                .setAuthor("Another")
                .setIsbn("9876543210-789")
                .setPrice(BigDecimal.valueOf(14.99))
                .setDescription("another")
                .setCoverImage("https://example.com/another")
                .setCategoryIds(Set.of(2L)));

        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto[].class);
        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Get book by id")
    @WithMockUser(username = "user", authorities = {"USER"})
    void getBookById_ShouldReturnBook() throws Exception {
        Long expectedId = 2L;
        BookDto expected = new BookDto().setId(2L)
                .setTitle("Another")
                .setAuthor("Another")
                .setIsbn("9876543210-789")
                .setPrice(BigDecimal.valueOf(14.99))
                .setDescription("another")
                .setCoverImage("https://example.com/another")
                .setCategoryIds(Set.of(2L));

        MvcResult result = mockMvc.perform(get("/books/{id}", expectedId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);

        assertEquals(expectedId, actual.getId());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Search book by params")
    @WithMockUser(username = "user", authorities = {"USER"})
    void searchBookByParams_ShouldReturnBooks() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto()
                .setId(2L)
                .setTitle("Another")
                .setAuthor("Another")
                .setIsbn("9876543210-789")
                .setPrice(BigDecimal.valueOf(14.99))
                .setDescription("another")
                .setCoverImage("https://example.com/another")
                .setCategoryIds(Set.of(2L)));

        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("authors", "Another")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class);
        assertEquals(1, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Update book by id")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateBookById_ShouldUpdateBook() throws Exception {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setTitle("Updated");
        requestDto.setAuthor("Updated");
        requestDto.setIsbn("9999999999-999");
        requestDto.setPrice(BigDecimal.valueOf(0.99));
        requestDto.setDescription("Updated");
        requestDto.setCoverImage("https://example.com/updated");
        requestDto.setCategoryIds(Set.of(1L));

        Long expectedId = 2L;
        BookDto expected = new BookDto();
        expected.setId(expectedId);
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setDescription(requestDto.getDescription());
        expected.setCoverImage(requestDto.getCoverImage());
        expected.setCategoryIds(requestDto.getCategoryIds());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/{id}", expectedId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertEquals(expectedId, actual.getId());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete book by id")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = {"classpath:database/add-temporary-book-to-db.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteBookById_ShouldReturnStatusNoContent() throws Exception {
        Long expectedId = 3L;

        mockMvc.perform(delete("/books/{id}", expectedId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
