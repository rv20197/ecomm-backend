package com.vatsalrajgor.eCommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vatsalrajgor.eCommerce.DTO.Product.ProductDTO;
import com.vatsalrajgor.eCommerce.DTO.Product.ProductResponse;
import com.vatsalrajgor.eCommerce.exceptions.APIException;
import com.vatsalrajgor.eCommerce.exceptions.ResourceNotFoundException;
import com.vatsalrajgor.eCommerce.mapper.ProductMapper;
import com.vatsalrajgor.eCommerce.model.Category;
import com.vatsalrajgor.eCommerce.model.Product;
import com.vatsalrajgor.eCommerce.repository.CategoryRepo;
import com.vatsalrajgor.eCommerce.repository.ProductRepo;

import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ProductService {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(CategoryRepo categoryRepo, ProductRepo productRepo, ProductMapper productMapper) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.productMapper = productMapper;
    }

    public ProductDTO addProduct(ProductDTO product, Long categoryId) {
        Category category = this.categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Product productEntity =  productMapper.toEntity(product);
        productEntity.setCategory(category);
        double specialPrice = product.getPrice() - ((productEntity.getDiscount() / 100) * product.getPrice());
        productEntity.setSpecialPrice(specialPrice);
        productEntity.setImage("default.png");
        Product savedProduct = productRepo.save(productEntity);
        return productMapper.toDTO(savedProduct);
    }

    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        String sortByField = sortBy.equals("categoryId") ? "productId" : sortBy;
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();
        Pageable pageDetails = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepo.findAll(pageDetails);
        List<Product> allProducts = productPage.getContent();
        if (allProducts.isEmpty()) {
            throw new APIException("No products found!");
        }
        List<ProductDTO> productDTOS = allProducts.stream().map(productMapper::toDTO).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber() + 1);
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        productResponse.setPageSize(productPage.getSize());
        return productResponse;
    }

    public ProductResponse getProductsByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy,
            String sortOrder) {
        String sortByField = sortBy.equals("categoryId") ? "productId" : sortBy;
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();
        Pageable pageDetails = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Page<Product> productsInCategory = productRepo.findByCategoryOrderByPriceAsc(category, pageDetails);

        if (productsInCategory.isEmpty()) {
            throw new APIException("No products found in this category!");
        }

        List<ProductDTO> productDTOS = productsInCategory.getContent().stream().map(productMapper::toDTO).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsInCategory.getNumber() + 1);
        productResponse.setTotalElements(productsInCategory.getTotalElements());
        productResponse.setTotalPages(productsInCategory.getTotalPages());
        productResponse.setLastPage(productsInCategory.isLast());
        productResponse.setPageSize(productsInCategory.getSize());
        return productResponse;
    }

    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        String sortByField = sortBy.equals("categoryId") ? "productId" : sortBy;
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();
        Pageable pageDetails = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Product> products = productRepo.findByProductNameLikeIgnoreCase(keyword, pageDetails);
        if (products.isEmpty()) {
            throw new APIException("No products found with this keyword!");
        }

        List<ProductDTO> productDTOS = products.getContent().stream().map(productMapper::toDTO).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(products.getNumber() + 1);
        productResponse.setTotalElements(products.getTotalElements());
        productResponse.setTotalPages(products.getTotalPages());
        productResponse.setLastPage(products.isLast());
        productResponse.setPageSize(products.getSize());
        return productResponse;
    }

    public ProductDTO updateProduct(Long productId, ProductDTO product) {
        Product existingProduct = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        Product productEntity = productMapper.toEntity(product);
        existingProduct.setProductName(productEntity.getProductName());
        existingProduct.setDescription(productEntity.getDescription());
        existingProduct.setQuantity(productEntity.getQuantity());
        existingProduct.setDiscount(productEntity.getDiscount());
        existingProduct.setPrice(productEntity.getPrice());

        double specialPrice = productEntity.getPrice() - ((productEntity.getDiscount() / 100) * productEntity.getPrice());
        existingProduct.setSpecialPrice(specialPrice);

        Product updatedProduct = productRepo.save(existingProduct);

        return productMapper.toDTO(updatedProduct);
    }

    public ProductDTO deleteProductById(@PathVariable Long productId) {
        Product deletedProduct = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepo.deleteById(productId);
        return productMapper.toDTO(deletedProduct);
    }
}
