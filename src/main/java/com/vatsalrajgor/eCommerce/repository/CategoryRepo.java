package com.vatsalrajgor.eCommerce.repository;

import com.vatsalrajgor.eCommerce.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);

    @EntityGraph(attributePaths = {"products"})
    Optional<Category> findWithProductsByCategoryId(Long categoryId);
}
