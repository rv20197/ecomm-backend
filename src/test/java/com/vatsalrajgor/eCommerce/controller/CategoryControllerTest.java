package com.vatsalrajgor.eCommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vatsalrajgor.eCommerce.DTO.Category.CategoryDTO;
import com.vatsalrajgor.eCommerce.DTO.Category.CategoryResponse;
import com.vatsalrajgor.eCommerce.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1L);
        categoryDTO.setCategoryName("Electronics");
    }

    @Test
    void getAllCategories_ShouldReturnCategoryResponse() throws Exception {
        CategoryResponse response = new CategoryResponse();
        response.setContent(List.of(categoryDTO));
        
        when(categoryService.getAllCategories(anyInt(), anyInt(), anyString(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].categoryName").value("Electronics"));
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(categoryDTO);

        mockMvc.perform(post("/api/public/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName").value("Electronics"));
    }

    @Test
    void deleteCategory_ShouldReturnOk() throws Exception {
        when(categoryService.deleteCategory(anyLong())).thenReturn(categoryDTO);

        mockMvc.perform(delete("/api/admin/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Electronics"));
    }

    @Test
    void updateCategory_ShouldReturnOk() throws Exception {
        when(categoryService.updateCategory(any(CategoryDTO.class), anyLong())).thenReturn(categoryDTO);

        mockMvc.perform(put("/api/public/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Electronics"));
    }
}
