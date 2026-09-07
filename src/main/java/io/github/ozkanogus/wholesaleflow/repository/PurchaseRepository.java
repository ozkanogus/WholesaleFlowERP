package io.github.ozkanogus.wholesaleflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.ozkanogus.wholesaleflow.domain.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
