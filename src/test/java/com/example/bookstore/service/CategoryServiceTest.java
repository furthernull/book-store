package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.category.CategoryRepository;
import com.example.bookstore.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final Long ID = 1L;
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;
    
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private Category category;
    private CategoryRequestDto categoryRequestDto;
    private CategoryDto categoryDto;

    @Test
    @DisplayName("Verify findAll() method was returned valid list")
    void findAll_ValidPageable_ShouldReturnAllCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(categoryPage)).thenReturn(List.of(categoryDto));

        List<CategoryDto> categoryDtos = categoryService.findAll(pageable);

        assertEquals(categoryDtos.size(), 1);
        assertEquals(categoryDtos.get(0), categoryDto);

        verify(categoryRepository, Mockito.times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify getById() method with valid id was returned valid Dto")
    void getById_ValidId_ShouldReturnCategoryDto() {
        when(categoryRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.getById(ID);

        assertEquals(categoryDto, actual);
        verify(categoryRepository, Mockito.times(1)).findById(ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify getById() method with not valid id that an exception id thrown")
    void getById_NotValidId_ShouldThrowException() {
        Long id = -1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class, () -> categoryService.getById(id));

        String expected = "Can't find category by id: " + id;
        String actual = ex.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, Mockito.times(1)).findById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName(" Verify save() method was returned valid response")
    void save_ValidCategoryRequestDto_ShouldReturnCategoryDto() {

        Category category = new Category();
        category.setId(1L);
        category.setName(categoryRequestDto.name());
        category.setDescription(categoryRequestDto.description());

        CategoryDto expected =
                new CategoryDto(category.getId(), category.getName(), category.getDescription());

        when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(categoryRequestDto);

        assertEquals(expected, actual);

        verify(categoryRepository, Mockito.times(1)).save(category);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify update() method has update category")
    void update_ValidCategoryRequestDto_ShouldReturnUpdatedCategoryDto() {
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        when(categoryService.update(ID, categoryRequestDto)).thenReturn(categoryDto);
        CategoryDto actual = categoryService.update(ID, categoryRequestDto);

        assertEquals(categoryDto, actual);
    }

    @Test
    @DisplayName("Verify update() method has return exception")
    void update_NotValidIdValidCategoryRequestDto_ShouldThrowException() {
        Long id = 100L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                        () -> categoryService.update(id, categoryRequestDto));

        String expected = String.format(
                "Can't update category. Category by id: %s doesn't exist", id);
        String actual = ex.getMessage();

        assertEquals(expected, actual);
    }

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("test category");
        category.setDescription("test category description");

        categoryRequestDto = new CategoryRequestDto(
                "test category",
                "test category description");

        categoryDto = new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription());
    }
}
