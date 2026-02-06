package com.vatsalrajgor.eCommerce.DTO.Category;

import com.vatsalrajgor.eCommerce.validation.SafeHtml;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long categoryId;
    @SafeHtml
    private String categoryName;
}
