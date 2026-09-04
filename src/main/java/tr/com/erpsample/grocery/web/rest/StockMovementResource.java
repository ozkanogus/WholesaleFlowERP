package tr.com.erpsample.grocery.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tr.com.erpsample.grocery.repository.StockMovementRepository;
import tr.com.erpsample.grocery.service.StockMovementService;
import tr.com.erpsample.grocery.service.dto.StockMovementDTO;
import tr.com.erpsample.grocery.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing
 * {@link tr.com.erpsample.grocery.domain.StockMovement}.
 */
@RestController
@RequestMapping("/api")
public class StockMovementResource {

	private final StockMovementService stockMovementService;

	private final StockMovementRepository stockMovementRepository;

	public StockMovementResource(StockMovementService stockMovementService,
			StockMovementRepository stockMovementRepository) {
		this.stockMovementService = stockMovementService;
		this.stockMovementRepository = stockMovementRepository;
	}

	/**
	 * {@code POST  /stockMovements} : Create a new stockMovement.
	 *
	 * @param stockMovementDTO the stockMovementDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new stockMovementDTO, or with status
	 *         {@code 400 (Bad Request)} if the stockMovement has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/stockMovements")
	public ResponseEntity<StockMovementDTO> createStockMovement(@Valid @RequestBody StockMovementDTO stockMovementDTO)
			throws URISyntaxException {
		if (stockMovementDTO.getId() != null) {
			throw new BadRequestAlertException("A new stockMovement cannot already have an ID");
		}
		StockMovementDTO result = stockMovementService.save(stockMovementDTO);
		return ResponseEntity.created(new URI("/api/stockMovements/" + result.getId())).body(result);
	}

	/**
	 * {@code PUT  /stockMovements} : Updates an existing stockMovement.
	 *
	 * @param stockMovementDTO the stockMovementDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated stockMovementDTO, or with status
	 *         {@code 400 (Bad Request)} if the stockMovementDTO is not valid, or
	 *         with status {@code 500 (Internal Server Error)} if the
	 *         stockMovementDTO couldn't be updated.
	 */
	@PutMapping("/stockMovements")
	public ResponseEntity<StockMovementDTO> updateStockMovement(@Valid @RequestBody StockMovementDTO stockMovementDTO) {
		if (stockMovementDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id");
		}

		if (!stockMovementRepository.existsById(stockMovementDTO.getId())) {
			throw new BadRequestAlertException("Entity not found");
		}

		StockMovementDTO result = stockMovementService.save(stockMovementDTO);
		return ResponseEntity.ok().body(result);
	}

	/**
	 * {@code GET  /stockMovements} : get all the stockMovements.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of stockMovements in body.
	 */
	@GetMapping("/stockMovements")
	public List<StockMovementDTO> getAllStockMovements() {
		return stockMovementService.findAll();
	}

	/**
	 * {@code GET  /stockMovements/:id} : get the "id" stockMovement.
	 *
	 * @param id the id of the stockMovementDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the stockMovementDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/stockMovements/{id}")
	public ResponseEntity<StockMovementDTO> getStockMovement(@PathVariable Long id) {
		Optional<StockMovementDTO> stockMovementDTO = stockMovementService.findOne(id);
		return ResponseEntity.of(stockMovementDTO);
	}

	/**
	 * {@code DELETE  /stockMovements/:id} : delete the "id" stockMovement.
	 *
	 * @param id the id of the stockMovementDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/stockMovements/{id}")
	public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {
		stockMovementService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
