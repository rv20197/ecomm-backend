package com.vatsalrajgor.eCommerce.service;

import com.vatsalrajgor.eCommerce.DTO.Cart.CartDTO;
import com.vatsalrajgor.eCommerce.DTO.Product.ProductDTO;
import com.vatsalrajgor.eCommerce.exceptions.APIException;
import com.vatsalrajgor.eCommerce.exceptions.ResourceNotFoundException;
import com.vatsalrajgor.eCommerce.mapper.CartMapper;
import com.vatsalrajgor.eCommerce.mapper.ProductMapper;
import com.vatsalrajgor.eCommerce.model.Cart;
import com.vatsalrajgor.eCommerce.model.CartItem;
import com.vatsalrajgor.eCommerce.model.Product;
import com.vatsalrajgor.eCommerce.repository.CartItemRepository;
import com.vatsalrajgor.eCommerce.repository.CartRepository;
import com.vatsalrajgor.eCommerce.repository.ProductRepo;
import com.vatsalrajgor.eCommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepo productRepo;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final AuthUtil authUtil;

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.getLoggedInUser());

        return cartRepository.save(cart);
    }

    @Transactional
    public CartDTO addToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (product.getQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " is out of stock.");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to quantity " + product.getQuantity() + ".");
        }

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setProductPrice(product.getSpecialPrice()); // Update price in case it changed
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setCart(cart);
            newCartItem.setQuantity(quantity);
            newCartItem.setDiscount(product.getDiscount());
            newCartItem.setProductPrice(product.getSpecialPrice());
            cartItemRepository.save(newCartItem);
            cart.getCartItems().add(newCartItem);
        }

        product.setQuantity(product.getQuantity() - quantity);

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        CartDTO cartDTO = cartMapper.toDTO(cart);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO productDTO = productMapper.toDTO(item.getProduct());
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }
}
