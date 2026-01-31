package com.vatsalrajgor.eCommerce.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final ProductMapper productMapper;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    @Autowired
    public ProductService(CategoryRepo categoryRepo, ProductRepo productRepo, ProductMapper productMapper, FileService fileService) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.productMapper = productMapper;
        this.fileService = fileService;
    }
    
    @Transactional
    public ProductDTO addProduct(ProductDTO product, Long categoryId) {
        Category category = this.categoryRepo.findWithProductsByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductPresent = category.getProducts().stream()
                .anyMatch(p -> p.getProductName().equalsIgnoreCase(product.getProductName()));

        if (isProductPresent) {
            throw new APIException("Product with name " + product.getProductName() + " already exists!");
        }

        Product productEntity =  productMapper.toEntity(product);
        productEntity.setCategory(category);
        double specialPrice = product.getPrice() - ((productEntity.getDiscount() / 100) * product.getPrice());
        productEntity.setSpecialPrice(specialPrice);
        productEntity.setImageName("default.png");
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
        return getProductResponse(productPage, productDTOS);
    }

    private ProductResponse getProductResponse(Page<Product> productPage, List<ProductDTO> productDTOS) {
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
        return getProductResponse(productsInCategory, productDTOS);
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
        return getProductResponse(products, productDTOS);
    }

    public ProductDTO updateProduct(Long productId, ProductDTO product) {
        Product existingProduct = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        Product productEntity = productMapper.toEntity(product);
        existingProduct.setProductName(productEntity.getProductName());
        existingProduct.setDescription(productEntity.getDescription());
        existingProduct.setQuantity(productEntity.getQuantity());
        existingProduct.setDiscount(productEntity.getDiscount());
        existingProduct.setPrice(productEntity.getPrice());
        existingProduct.setImageName(productEntity.getImageName());

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

    public ProductDTO uploadProductImage(Long productId, MultipartFile image) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image cannot be null or empty");
        }
        try {
            String fileName = fileService.uploadImage(path, image);
            product.setImageName(fileName);
        } catch (IOException e) {
            throw new APIException("Failed to upload image: " + e.getMessage());
        }
        Product updatedProduct = productRepo.save(product);
        return productMapper.toDTO(updatedProduct);
    }
}
