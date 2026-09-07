package io.github.ozkanogus.wholesaleflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.ozkanogus.wholesaleflow.domain.StockMovement;
import io.github.ozkanogus.wholesaleflow.domain.enumeration.OperationType;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
	Optional<StockMovement> findByOperationIdAndProductIdAndOperationType(Long oparationId, Long productId,
			OperationType operationType);

	void deleteByOperationId(Long id);
}
