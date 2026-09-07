package io.github.ozkanogus.wholesaleflow.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.ozkanogus.wholesaleflow.domain.Purchase;
import io.github.ozkanogus.wholesaleflow.domain.Sale;
import io.github.ozkanogus.wholesaleflow.domain.StockMovement;
import io.github.ozkanogus.wholesaleflow.domain.enumeration.OperationType;
import io.github.ozkanogus.wholesaleflow.repository.StockMovementRepository;
import io.github.ozkanogus.wholesaleflow.service.dto.StockMovementDTO;
import io.github.ozkanogus.wholesaleflow.service.mapper.StockMovementMapper;

/**
 * Service Implementation for managing {@link StockMovement}.
 */
@Service
@Transactional
public class StockMovementService {

	private final StockMovementRepository stockMovementRepository;

	private final StockMovementMapper stockMovementMapper;

	public StockMovementService(StockMovementRepository stockMovementRepository,
			StockMovementMapper stockMovementMapper) {
		this.stockMovementRepository = stockMovementRepository;
		this.stockMovementMapper = stockMovementMapper;
	}

	/**
	 * Save a stockMovement.
	 *
	 * @param stockMovementDTO the entity to save.
	 * @return the persisted entity.
	 */
	public StockMovementDTO save(StockMovementDTO stockMovementDTO) {
		StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);
		stockMovement = stockMovementRepository.save(stockMovement);
		return stockMovementMapper.toDto(stockMovement);
	}

	/**
	 * Get all the stockMovements.
	 *
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	public List<StockMovementDTO> findAll() {
		return stockMovementRepository.findAll().stream().map(stockMovementMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one stockMovement by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	public Optional<StockMovementDTO> findOne(Long id) {
		return stockMovementRepository.findById(id).map(stockMovementMapper::toDto);
	}

	/**
	 * Delete the stockMovement by id.
	 *
	 * @param id the id of the entity.
	 */
	public void delete(Long id) {
		stockMovementRepository.deleteById(id);
	}

	public void updateStockMovement(OperationType operationType, Object o) {
		if (operationType == OperationType.SALE) {
			Sale operation = (Sale) o;
			deleteByOperationId(operation.getId());
			operation.getProducts().forEach(_product -> {
				StockMovement stockMovement = new StockMovement();
				stockMovement.setGrocery(operation.getGrocery());
				stockMovement.setProduct(_product.getProduct());
				stockMovement.setOperationId(operation.getId());
				stockMovement.setOperationType(operationType);
				stockMovement.setCount((operationType == OperationType.SALE || operationType == OperationType.GARBAGE)
						? _product.getCount().negate()
						: _product.getCount());
				stockMovementRepository.save(stockMovement);
			});

		} else if (operationType == OperationType.PURCHASE) {
			Purchase operation = (Purchase) o;
			deleteByOperationId(operation.getId());
			operation.getProducts().forEach(_product -> {
				StockMovement stockMovement = new StockMovement();
				stockMovement.setGrocery(operation.getGrocery());
				stockMovement.setProduct(_product.getProduct());
				stockMovement.setOperationId(operation.getId());
				stockMovement.setOperationType(operationType);
				stockMovement.setCount((operationType == OperationType.SALE || operationType == OperationType.GARBAGE)
						? _product.getCount().negate()
						: _product.getCount());
				stockMovementRepository.save(stockMovement);
			});
		}
	}

	public void deleteByOperationId(Long operationId) {
		stockMovementRepository.deleteByOperationId(operationId);
	}

}
