package io.github.ozkanogus.wholesaleflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.ozkanogus.wholesaleflow.domain.Grocery;

/**
 * Spring Data SQL repository for the Grocery entity.
 */
@Repository
public interface GroceryRepository extends JpaRepository<Grocery, Long> {

	Optional<Grocery> findByNameIgnoreCase(String name);
}
