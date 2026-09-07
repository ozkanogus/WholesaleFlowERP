package io.github.ozkanogus.wholesaleflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.ozkanogus.wholesaleflow.domain.Product;

/**
 * Spring Data SQL repository for the Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByNameIgnoreCase(String name);
}
