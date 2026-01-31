package com.vatsalrajgor.eCommerce.service;

import com.vatsalrajgor.eCommerce.DTO.Category.CategoryDTO;
import com.vatsalrajgor.eCommerce.DTO.Category.CategoryResponse;
import com.vatsalrajgor.eCommerce.exceptions.APIException;
import com.vatsalrajgor.eCommerce.exceptions.ResourceNotFoundException;
import com.vatsalrajgor.eCommerce.mapper.CategoryMapper;
import com.vatsalrajgor.eCommerce.model.Category;
import com.vatsalrajgor.eCommerce.repository.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");

        categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1L);
        categoryDTO.setCategoryName("Electronics");
    }

    @Test
    void getAllCategories_ShouldReturnCategoryResponse() {
        Page<Category> categoryPage = new PageImpl<>(List.of(category));
        when(categoryRepo.findAll(any(Pageable.class))).thenReturn(categoryPage);
        when(categoryMapper.toDTO(any(Category.class))).thenReturn(categoryDTO);

        CategoryResponse response = categoryService.getAllCategories(1, 10, "categoryId", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Electronics", response.getContent().getFirst().getCategoryName());
        verify(categoryRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllCategories_ShouldThrowAPIException_WhenNoCategoriesFound() {
        Page<Category> emptyPage = new PageImpl<>(Collections.emptyList());
        when(categoryRepo.findAll(any(Pageable.class))).thenReturn(emptyPage);

        assertThrows(APIException.class, () -> categoryService.getAllCategories(1, 10, "categoryId", "asc"));
    }

    @Test
    void createCategory_ShouldReturnCategoryDTO() {
        when(categoryMapper.toEntity(any(CategoryDTO.class))).thenReturn(category);
        when(categoryRepo.findByCategoryName(anyString())).thenReturn(null);
        when(categoryRepo.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDTO(any(Category.class))).thenReturn(categoryDTO);

        CategoryDTO savedCategory = categoryService.createCategory(categoryDTO);

        assertNotNull(savedCategory);
        assertEquals("Electronics", savedCategory.getCategoryName());
        verify(categoryRepo, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowAPIException_WhenCategoryAlreadyExists() {
        when(categoryRepo.findByCategoryName(anyString())).thenReturn(category);

        assertThrows(APIException.class, () -> categoryService.createCategory(categoryDTO));
    }

    @Test
    void deleteCategory_ShouldReturnCategoryDTO() {
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(any(Category.class))).thenReturn(categoryDTO);

        CategoryDTO deletedCategory = categoryService.deleteCategory(1L);

        assertNotNull(deletedCategory);
        assertEquals("Electronics", deletedCategory.getCategoryName());
        verify(categoryRepo, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteCategory_ShouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategoryDTO() {
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepo.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDTO(any(Category.class))).thenReturn(categoryDTO);

        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO, 1L);

        assertNotNull(updatedCategory);
        assertEquals("Electronics", updatedCategory.getCategoryName());
        verify(categoryRepo, times(1)).save(any(Category.class));
    }
}
