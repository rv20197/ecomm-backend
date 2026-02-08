package com.vatsalrajgor.eCommerce.mapper;

import com.vatsalrajgor.eCommerce.DTO.Cart.CartDTO;
import com.vatsalrajgor.eCommerce.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDTO toDTO(Cart cart);
    Cart toEntity(CartDTO cartDTO);
}
