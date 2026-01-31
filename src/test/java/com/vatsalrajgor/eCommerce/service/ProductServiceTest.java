package com.vatsalrajgor.eCommerce.service;

import com.vatsalrajgor.eCommerce.DTO.Product.ProductDTO;
import com.vatsalrajgor.eCommerce.DTO.Product.ProductResponse;
import com.vatsalrajgor.eCommerce.exceptions.APIException;
import com.vatsalrajgor.eCommerce.mapper.ProductMapper;
import com.vatsalrajgor.eCommerce.model.Category;
import com.vatsalrajgor.eCommerce.model.Product;
import com.vatsalrajgor.eCommerce.repository.CategoryRepo;
import com.vatsalrajgor.eCommerce.repository.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private FileService fileService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");
        category.setProducts(new ArrayList<>());

        product = new Product();
        product.setProductId(1L);
        product.setProductName("Laptop");
        product.setPrice(1000.0);
        product.setDiscount(10.0);
        product.setCategory(category);

        productDTO = new ProductDTO();
        productDTO.setProductId(1L);
        productDTO.setProductName("Laptop");
        productDTO.setPrice(1000.0);
        productDTO.setDiscount(10.0);
    }

    @Test
    void addProduct_ShouldReturnProductDTO() {
        when(categoryRepo.findWithProductsByCategoryId(anyLong())).thenReturn(Optional.of(category));
        when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(product);
        when(productRepo.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        ProductDTO savedProduct = productService.addProduct(productDTO, 1L);

        assertNotNull(savedProduct);
        assertEquals("Laptop", savedProduct.getProductName());
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void addProduct_ShouldThrowAPIException_WhenProductAlreadyExists() {
        category.getProducts().add(product);
        when(categoryRepo.findWithProductsByCategoryId(anyLong())).thenReturn(Optional.of(category));

        assertThrows(APIException.class, () -> productService.addProduct(productDTO, 1L));
    }

    @Test
    void getAllProducts_ShouldReturnProductResponse() {
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepo.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        ProductResponse response = productService.getAllProducts(1, 10, "productId", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(productRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllProducts_ShouldThrowAPIException_WhenNoProductsFound() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productRepo.findAll(any(Pageable.class))).thenReturn(emptyPage);

        assertThrows(APIException.class, () -> productService.getAllProducts(1, 10, "productId", "asc"));
    }

    @Test
    void getProductsByCategory_ShouldReturnProductResponse() {
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(category));
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepo.findByCategoryOrderByPriceAsc(any(Category.class), any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        ProductResponse response = productService.getProductsByCategory(1L, 1, 10, "productId", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void searchProductByKeyword_ShouldReturnProductResponse() {
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepo.findByProductNameLikeIgnoreCase(anyString(), any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        ProductResponse response = productService.searchProductByKeyword("Laptop", 1, 10, "productId", "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProductDTO() {
        when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
        when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(product);
        when(productRepo.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        ProductDTO updatedProduct = productService.updateProduct(1L, productDTO);

        assertNotNull(updatedProduct);
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProductById_ShouldReturnProductDTO() {
        when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        ProductDTO deletedProduct = productService.deleteProductById(1L);

        assertNotNull(deletedProduct);
        verify(productRepo, times(1)).deleteById(1L);
    }

    @Test
    void uploadProductImage_ShouldReturnUpdatedProductDTO() throws IOException {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
        when(fileService.uploadImage(any(), any(MultipartFile.class))).thenReturn("new_image.jpg");
        when(productRepo.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        ProductDTO updatedProduct = productService.uploadProductImage(1L, image);

        assertNotNull(updatedProduct);
        assertEquals("new_image.jpg", product.getImageName());
        verify(productRepo, times(1)).save(any(Product.class));
    }
}
