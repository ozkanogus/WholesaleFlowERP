package io.github.ozkanogus.wholesaleflow.web.rest;

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

import io.github.ozkanogus.wholesaleflow.repository.PurchaseRepository;
import io.github.ozkanogus.wholesaleflow.service.PurchaseService;
import io.github.ozkanogus.wholesaleflow.service.dto.PurchaseDTO;
import io.github.ozkanogus.wholesaleflow.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link io.github.ozkanogus.wholesaleflow.domain.Purchase}.
 */
@RestController
@RequestMapping("/api")
public class PurchaseResource {

	private final PurchaseService purchaseService;

	private final PurchaseRepository purchaseRepository;

	public PurchaseResource(PurchaseService purchaseService, PurchaseRepository purchaseRepository) {
		this.purchaseService = purchaseService;
		this.purchaseRepository = purchaseRepository;
	}

	/**
	 * {@code POST  /purchases} : Create a new purchase.
	 *
	 * @param purchaseDTO the purchaseDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new purchaseDTO, or with status {@code 400 (Bad Request)} if
	 *         the purchase has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/purchases")
	public ResponseEntity<PurchaseDTO> createPurchase(@Valid @RequestBody PurchaseDTO purchaseDTO)
			throws URISyntaxException {
		if (purchaseDTO.getId() != null) {
			throw new BadRequestAlertException("A new purchase cannot already have an ID");
		}
		PurchaseDTO result = purchaseService.save(purchaseDTO);
		return ResponseEntity.created(new URI("/api/purchases/" + result.getId())).body(result);
	}

	/**
	 * {@code PUT  /purchases} : Updates an existing purchase.
	 *
	 * @param purchaseDTO the purchaseDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated purchaseDTO, or with status {@code 400 (Bad Request)} if
	 *         the purchaseDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the purchaseDTO couldn't be
	 *         updated.
	 */
	@PutMapping("/purchases")
	public ResponseEntity<PurchaseDTO> updatePurchase(@Valid @RequestBody PurchaseDTO purchaseDTO) {
		if (purchaseDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id");
		}

		if (!purchaseRepository.existsById(purchaseDTO.getId())) {
			throw new BadRequestAlertException("Entity not found");
		}

		PurchaseDTO result = purchaseService.save(purchaseDTO);
		return ResponseEntity.ok().body(result);
	}

	/**
	 * {@code GET  /purchases} : get all the purchases.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of purchases in body.
	 */
	@GetMapping("/purchases")
	public List<PurchaseDTO> getAllPurchases() {
		return purchaseService.findAll();
	}

	/**
	 * {@code GET  /purchases/:id} : get the "id" purchase.
	 *
	 * @param id the id of the purchaseDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the purchaseDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/purchases/{id}")
	public ResponseEntity<PurchaseDTO> getPurchase(@PathVariable Long id) {
		Optional<PurchaseDTO> purchaseDTO = purchaseService.findOne(id);
		return ResponseEntity.of(purchaseDTO);
	}

	/**
	 * {@code DELETE  /purchases/:id} : delete the "id" purchase.
	 *
	 * @param id the id of the purchaseDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/purchases/{id}")
	public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
		purchaseService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
