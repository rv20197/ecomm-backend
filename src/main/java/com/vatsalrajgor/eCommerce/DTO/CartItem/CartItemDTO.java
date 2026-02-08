package com.vatsalrajgor.eCommerce.DTO.CartItem;

import com.vatsalrajgor.eCommerce.DTO.Cart.CartDTO;
import com.vatsalrajgor.eCommerce.DTO.Product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private CartDTO cartDTO;
    private ProductDTO productDTO;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
