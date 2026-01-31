package com.vatsalrajgor.eCommerce.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vatsalrajgor.eCommerce.DTO.Product.ProductDTO;
import com.vatsalrajgor.eCommerce.DTO.Product.ProductResponse;
import com.vatsalrajgor.eCommerce.service.ProductService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO product, @PathVariable Long categoryId){
        ProductDTO productDTO = productService.addProduct(product, categoryId);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "pageNumber", defaultValue = "${eCommerce.pageNumber}") Integer pageNumber,
                                                          @RequestParam(name = "pageSize", defaultValue = "${eCommerce.pageSize}") Integer pageSize,
                                                          @RequestParam(name="sortBy", defaultValue = "${eCommerce.sortBy}") String sortBy,
                                                          @RequestParam(name = "sortOrder", defaultValue = "${eCommerce.sortOrder}") String sortOrder){
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                             @RequestParam(name = "pageNumber", defaultValue = "${eCommerce.pageNumber}") Integer pageNumber,
                                                             @RequestParam(name = "pageSize", defaultValue = "${eCommerce.pageSize}") Integer pageSize,
                                                             @RequestParam(name="sortBy", defaultValue = "${eCommerce.sortBy}") String sortBy,
                                                             @RequestParam(name = "sortOrder", defaultValue = "${eCommerce.sortOrder}") String sortOrder){
        ProductResponse productResponse = productService.getProductsByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name = "pageNumber", defaultValue = "${eCommerce.pageNumber}") Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = "${eCommerce.pageSize}") Integer pageSize,
                                                                @RequestParam(name="sortBy", defaultValue = "${eCommerce.sortBy}") String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = "${eCommerce.sortOrder}") String sortOrder){
        ProductResponse productResponse = productService.searchProductByKeyword('%'+keyword+'%',pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProductById(@PathVariable Long productId, @Valid @RequestBody ProductDTO product){
        ProductDTO updatedProductDTO = productService.updateProduct(productId, product);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProductById(@PathVariable Long productId){
        ProductDTO deletedProduct = productService.deleteProductById(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    @PutMapping(value ="/admin/products/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestPart("image") MultipartFile image){
        ProductDTO updatedProductDTO = productService.uploadProductImage(productId, image);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }
}
