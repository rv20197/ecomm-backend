package com.vatsalrajgor.eCommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vatsalrajgor.eCommerce.DTO.Product.ProductDTO;
import com.vatsalrajgor.eCommerce.DTO.Product.ProductResponse;
import com.vatsalrajgor.eCommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setProductId(1L);
        productDTO.setProductName("Laptop");
        productDTO.setPrice(1000.0);
    }

    @Test
    void addProduct_ShouldReturnCreated() throws Exception {
        when(productService.addProduct(any(ProductDTO.class), anyLong())).thenReturn(productDTO);

        mockMvc.perform(post("/api/admin/categories/1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }

    @Test
    void getAllProducts_ShouldReturnOk() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setContent(List.of(productDTO));

        when(productService.getAllProducts(anyInt(), anyInt(), anyString(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/public/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Laptop"));
    }

    @Test
    void getProductsByCategory_ShouldReturnOk() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setContent(List.of(productDTO));

        when(productService.getProductsByCategory(anyLong(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/public/categories/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Laptop"));
    }

    @Test
    void getProductsByKeyword_ShouldReturnOk() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setContent(List.of(productDTO));

        when(productService.searchProductByKeyword(anyString(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/public/products/keyword/Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Laptop"));
    }

    @Test
    void updateProductById_ShouldReturnOk() throws Exception {
        when(productService.updateProduct(anyLong(), any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(put("/api/admin/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }

    @Test
    void deleteProductById_ShouldReturnOk() throws Exception {
        when(productService.deleteProductById(anyLong())).thenReturn(productDTO);

        mockMvc.perform(delete("/api/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }

    @Test
    void updateProductImage_ShouldReturnOk() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        when(productService.uploadProductImage(anyLong(), any())).thenReturn(productDTO);

        mockMvc.perform(multipart("/api/admin/products/1/image")
                .file(image)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }
}
