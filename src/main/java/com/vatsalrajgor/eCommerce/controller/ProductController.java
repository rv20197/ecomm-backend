package com.vatsalrajgor.eCommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vatsalrajgor.eCommerce.DTO.Product.ProductDTO;
import com.vatsalrajgor.eCommerce.DTO.Product.ProductResponse;
import com.vatsalrajgor.eCommerce.config.PaginationProperties;
import com.vatsalrajgor.eCommerce.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final PaginationProperties paginationProperties;

    @Autowired
    public ProductController(ProductService productService, PaginationProperties paginationProperties){
        this.productService = productService;
        this.paginationProperties = paginationProperties;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO product, @PathVariable Long categoryId){
        ProductDTO productDTO = productService.addProduct(product, categoryId);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                                          @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                          @RequestParam(name="sortBy", required = false) String sortBy,
                                                          @RequestParam(name = "sortOrder", required = false) String sortOrder){
        int pgNum = pageNumber != null ? pageNumber : paginationProperties.getPageNumber();
        int pgSize = pageSize != null ? pageSize : paginationProperties.getPageSize();
        String sortByParam = sortBy != null ? sortBy : paginationProperties.getSortBy();
        String sortOrderParam = sortOrder != null ? sortOrder : paginationProperties.getSortOrder();
        ProductResponse productResponse = productService.getAllProducts(pgNum,pgSize,sortByParam,sortOrderParam);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                             @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                                             @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                             @RequestParam(name="sortBy", required = false) String sortBy,
                                                             @RequestParam(name = "sortOrder", required = false) String sortOrder){
        int pgNum = pageNumber != null ? pageNumber : paginationProperties.getPageNumber();
        int pgSize = pageSize != null ? pageSize : paginationProperties.getPageSize();
        String sortByParam = sortBy != null ? sortBy : paginationProperties.getSortBy();
        String sortOrderParam = sortOrder != null ? sortOrder : paginationProperties.getSortOrder();
        ProductResponse productResponse = productService.getProductsByCategory(categoryId,pgNum,pgSize,sortByParam,sortOrderParam);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,  @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                                @RequestParam(name="sortBy", required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", required = false) String sortOrder){
        int pgNum = pageNumber != null ? pageNumber : paginationProperties.getPageNumber();
        int pgSize = pageSize != null ? pageSize : paginationProperties.getPageSize();
        String sortByParam = sortBy != null ? sortBy : paginationProperties.getSortBy();
        String sortOrderParam = sortOrder != null ? sortOrder : paginationProperties.getSortOrder();
        ProductResponse productResponse = productService.searchProductByKeyword('%'+keyword+'%',pgNum,pgSize,sortByParam,sortOrderParam);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProductById(@PathVariable Long productId, @RequestBody ProductDTO product){
        ProductDTO updatedProductDTO = productService.updateProduct(productId, product);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProductById(@PathVariable Long productId){
        ProductDTO deletedProduct = productService.deleteProductById(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }
}
