package com.vatsalrajgor.eCommerce.controller;

import com.vatsalrajgor.eCommerce.DTO.Category.CategoryDTO;
import com.vatsalrajgor.eCommerce.DTO.Category.CategoryResponse;
import com.vatsalrajgor.eCommerce.service.CategoryService;
import com.vatsalrajgor.eCommerce.config.PaginationProperties;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;
    private final PaginationProperties paginationProperties;

    @Autowired
    public CategoryController(CategoryService categoryService, PaginationProperties paginationProperties) {
        this.categoryService = categoryService;
        this.paginationProperties = paginationProperties;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                                             @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                             @RequestParam(name="sortBy", required = false) String sortBy,
                                                             @RequestParam(name = "sortOrder", required = false) String sortOrder){
        int pgNum = pageNumber != null ? pageNumber : paginationProperties.getPageNumber();
        int pgSize = pageSize != null ? pageSize : paginationProperties.getPageSize();
        String sortByParam = sortBy != null ? sortBy : paginationProperties.getSortBy();
        String sortOrderParam = sortOrder != null ? sortOrder : paginationProperties.getSortOrder();
        return new ResponseEntity<>(categoryService.getAllCategories(pgNum, pgSize,sortByParam,sortOrderParam), HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category){
        return new ResponseEntity<>(categoryService.createCategory(category), HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@Valid @PathVariable Long categoryId){
        return new ResponseEntity<>(categoryService.deleteCategory(categoryId), HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO category, @PathVariable Long categoryId){
        return new ResponseEntity<>(categoryService.updateCategory(category, categoryId), HttpStatus.OK);
    }
}
