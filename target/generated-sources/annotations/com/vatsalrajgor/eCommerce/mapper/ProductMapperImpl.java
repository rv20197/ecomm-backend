package com.vatsalrajgor.eCommerce.mapper;

import com.vatsalrajgor.eCommerce.DTO.Product.ProductDTO;
import com.vatsalrajgor.eCommerce.model.Product;
import java.util.Arrays;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-30T20:06:57+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO toDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        productDTO.setProductId( product.getProductId() );
        productDTO.setProductName( product.getProductName() );
        productDTO.setDescription( product.getDescription() );
        productDTO.setImageName( product.getImageName() );
        byte[] image = product.getImage();
        if ( image != null ) {
            productDTO.setImage( Arrays.copyOf( image, image.length ) );
        }
        productDTO.setQuantity( product.getQuantity() );
        productDTO.setPrice( product.getPrice() );
        productDTO.setDiscount( product.getDiscount() );
        productDTO.setSpecialPrice( product.getSpecialPrice() );

        return productDTO;
    }

    @Override
    public Product toEntity(ProductDTO productDTO) {
        if ( productDTO == null ) {
            return null;
        }

        Product product = new Product();

        product.setProductId( productDTO.getProductId() );
        product.setProductName( productDTO.getProductName() );
        product.setDescription( productDTO.getDescription() );
        product.setImageName( productDTO.getImageName() );
        byte[] image = productDTO.getImage();
        if ( image != null ) {
            product.setImage( Arrays.copyOf( image, image.length ) );
        }
        product.setQuantity( productDTO.getQuantity() );
        product.setPrice( productDTO.getPrice() );
        product.setDiscount( productDTO.getDiscount() );
        product.setSpecialPrice( productDTO.getSpecialPrice() );

        return product;
    }
}
