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

import io.github.ozkanogus.wholesaleflow.repository.GroceryRepository;
import io.github.ozkanogus.wholesaleflow.service.GroceryService;
import io.github.ozkanogus.wholesaleflow.service.dto.GroceryDTO;
import io.github.ozkanogus.wholesaleflow.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link io.github.ozkanogus.wholesaleflow.domain.Grocery}.
 */
@RestController
@RequestMapping("/api")
public class GroceryResource {

	private final GroceryService groceryService;

	private final GroceryRepository groceryRepository;

	public GroceryResource(GroceryService groceryService, GroceryRepository groceryRepository) {
		this.groceryService = groceryService;
		this.groceryRepository = groceryRepository;
	}

	/**
	 * {@code POST  /groceries} : Create a new grocery.
	 *
	 * @param groceryDTO the groceryDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new groceryDTO, or with status {@code 400 (Bad Request)} if
	 *         the grocery has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/groceries")
	public ResponseEntity<GroceryDTO> createGrocery(@Valid @RequestBody GroceryDTO groceryDTO)
			throws URISyntaxException {
		if (groceryDTO.getId() != null) {
			throw new BadRequestAlertException("A new grocery cannot already have an ID");
		}
		GroceryDTO result = groceryService.save(groceryDTO);
		return ResponseEntity.created(new URI("/api/groceries/" + result.getId())).body(result);
	}

	/**
	 * {@code PUT  /groceries} : Updates an existing grocery.
	 *
	 * @param groceryDTO the groceryDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated groceryDTO, or with status {@code 400 (Bad Request)} if
	 *         the groceryDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the groceryDTO couldn't be
	 *         updated.
	 */
	@PutMapping("/groceries")
	public ResponseEntity<GroceryDTO> updateGrocery(@Valid @RequestBody GroceryDTO groceryDTO) {
		if (groceryDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id");
		}

		if (!groceryRepository.existsById(groceryDTO.getId())) {
			throw new BadRequestAlertException("Entity not found");
		}

		GroceryDTO result = groceryService.save(groceryDTO);
		return ResponseEntity.ok().body(result);
	}

	/**
	 * {@code GET  /groceries} : get all the groceries.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of groceries in body.
	 */
	@GetMapping("/groceries")
	public List<GroceryDTO> getAllGroceries() {
		return groceryService.findAll();
	}

	/**
	 * {@code GET  /groceries/:id} : get the "id" grocery.
	 *
	 * @param id the id of the groceryDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the groceryDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/groceries/{id}")
	public ResponseEntity<GroceryDTO> getProduct(@PathVariable Long id) {
		Optional<GroceryDTO> groceryDTO = groceryService.findOne(id);
		return ResponseEntity.of(groceryDTO);
	}

	/**
	 * {@code DELETE  /groceries/:id} : delete the "id" grocery.
	 *
	 * @param id the id of the groceryDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/groceries/{id}")
	public ResponseEntity<Void> deleteGrocery(@PathVariable Long id) {
		groceryService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
