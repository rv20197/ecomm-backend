package com.vatsalrajgor.eCommerce.repository;

import com.vatsalrajgor.eCommerce.model.AppRole;
import com.vatsalrajgor.eCommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
