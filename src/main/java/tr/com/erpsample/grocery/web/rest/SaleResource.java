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

import tr.com.erpsample.grocery.repository.SaleRepository;
import tr.com.erpsample.grocery.service.SaleService;
import tr.com.erpsample.grocery.service.dto.ProductDTO;
import tr.com.erpsample.grocery.service.dto.SaleDTO;
import tr.com.erpsample.grocery.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link tr.com.erpsample.grocery.domain.Sale}.
 */
@RestController
@RequestMapping("/api")
public class SaleResource {

	private final SaleService saleService;

	private final SaleRepository saleRepository;

	public SaleResource(SaleService saleService, SaleRepository saleRepository) {
		this.saleService = saleService;
		this.saleRepository = saleRepository;
	}

	/**
	 * {@code POST  /sales} : Create a new sale.
	 *
	 * @param saleDTO the saleDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new saleDTO, or with status {@code 400 (Bad Request)} if the
	 *         sale has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/sales")
	public ResponseEntity<SaleDTO> createSale(@Valid @RequestBody SaleDTO saleDTO) throws URISyntaxException {
		if (saleDTO.getId() != null) {
			throw new BadRequestAlertException("A new sale cannot already have an ID");
		}
		SaleDTO result = saleService.save(saleDTO);
		return ResponseEntity.created(new URI("/api/sales/" + result.getId())).body(result);
	}

	/**
	 * {@code PUT  /sales} : Updates an existing sale.
	 *
	 * @param saleDTO the saleDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated saleDTO, or with status {@code 400 (Bad Request)} if the
	 *         saleDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the saleDTO couldn't be
	 *         updated.
	 */
	@PutMapping("/sales")
	public ResponseEntity<SaleDTO> updateSale(@Valid @RequestBody SaleDTO saleDTO) {
		if (saleDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id");
		}

		if (!saleRepository.existsById(saleDTO.getId())) {
			throw new BadRequestAlertException("Entity not found");
		}

		SaleDTO result = saleService.save(saleDTO);
		return ResponseEntity.ok().body(result);
	}

	/**
	 * {@code GET  /sales} : get all the sales.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of sales in body.
	 */
	@GetMapping("/sales")
	public List<SaleDTO> getAllSales() {
		return saleService.findAll();
	}

	/**
	 * {@code GET  /sales/:id} : get the "id" sale.
	 *
	 * @param id the id of the saleDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the saleDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/sales/{id}")
	public ResponseEntity<SaleDTO> getSale(@PathVariable Long id) {
		Optional<SaleDTO> saleDTO = saleService.findOne(id);
		return ResponseEntity.of(saleDTO);
	}

	/**
	 * {@code DELETE  /sales/:id} : delete the "id" sale.
	 *
	 * @param id the id of the saleDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/sales/{id}")
	public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
		saleService.delete(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * {@code GET  /sales/topSold/:groceryId} : get the "groceryId" Grocery.
	 *
	 * @param groceryId the id of the Grocery to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of sales in body.
	 */
	@GetMapping("/sales/topSold/{groceryId}")
	public List<ProductDTO> getTopSoldThreeProduct(@PathVariable Long groceryId) {
		return saleService.findTopSoldThreeProduct(groceryId);
	}
}
