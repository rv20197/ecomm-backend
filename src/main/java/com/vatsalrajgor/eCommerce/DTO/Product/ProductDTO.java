package com.vatsalrajgor.eCommerce.DTO.Product;

import com.vatsalrajgor.eCommerce.validation.SafeHtml;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    @SafeHtml
    private String productName;
    @SafeHtml
    private String description;
    @SafeHtml
    private String imageName;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;
}
