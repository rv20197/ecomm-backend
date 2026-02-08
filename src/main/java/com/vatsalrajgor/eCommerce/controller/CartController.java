package com.vatsalrajgor.eCommerce.controller;

import com.vatsalrajgor.eCommerce.DTO.Cart.CartDTO;
import com.vatsalrajgor.eCommerce.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/product/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addToCart(productId,quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }
}
