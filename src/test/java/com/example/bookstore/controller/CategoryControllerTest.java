package com.example.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
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
    @DisplayName("Create a new category")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCategory_ValidRequestDto_Success() throws Exception {
        CategoryRequestDto requestDto =
                new CategoryRequestDto("Test Category", "Test description");

        CategoryDto expected = new CategoryDto(1L, requestDto.name(), requestDto.description());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertEquals(expected.name(), actual.name());
        assertEquals(expected.description(), actual.description());
    }

    @Test
    @DisplayName("Get all categories")
    @WithMockUser(username = "user", authorities = {"USER"})
    void getAllCategories_ShouldReturnAllCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> actual = List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto[].class));

        assertEquals(2, actual.size());
    }

    @Test
    @DisplayName("Get category by id")
    @WithMockUser(username = "user", authorities = {"USER"})
    void getCategoryById_ShouldReturnCategory() throws Exception {
        Long expectedId = 1L;
        MvcResult result = mockMvc.perform(get("/categories/{id}", expectedId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertEquals(expectedId, actual.id());
    }

    @Test
    @DisplayName("Update category by id")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateCategoryById_ShouldUpdateCategory() throws Exception {
        Long categoryId = 1L;
        CategoryRequestDto requestDto = new CategoryRequestDto(
                "Updated Name",
                "Updated description");

        CategoryDto expected = new CategoryDto(
                categoryId,
                requestDto.name(),
                requestDto.description());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertEquals(expected.id(), actual.id());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete category by id")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = {"classpath:database/add-temporary-category-to-db.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteCategoryById_ShouldReturnStatusNoContent() throws Exception {
        Long categoryId = 4L;

        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Get books by category id")
    @WithMockUser(username = "user", authorities = {"USER"})
    void getBooksByCategoryId_ShouldReturnBooks() throws Exception {
        Long categoryId = 1L;
        MvcResult result = mockMvc.perform(get("/categories/{id}/books", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> actual = List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDtoWithoutCategoryIds[].class));

        assertEquals(1, actual.size());
    }
}
