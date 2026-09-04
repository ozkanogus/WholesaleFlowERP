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

import tr.com.erpsample.grocery.repository.ProductRepository;
import tr.com.erpsample.grocery.service.ProductService;
import tr.com.erpsample.grocery.service.dto.ProductDTO;
import tr.com.erpsample.grocery.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link tr.com.erpsample.grocery.domain.Product}.
 */
@RestController
@RequestMapping("/api")
public class ProductResource {

	private final ProductService productService;

	private final ProductRepository productRepository;

	public ProductResource(ProductService productService, ProductRepository productRepository) {
		this.productService = productService;
		this.productRepository = productRepository;
	}

	/**
	 * {@code POST  /products} : Create a new product.
	 *
	 * @param productDTO the productDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new productDTO, or with status {@code 400 (Bad Request)} if
	 *         the product has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/products")
	public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO)
			throws URISyntaxException {
		if (productDTO.getId() != null) {
			throw new BadRequestAlertException("A new product cannot already have an ID");
		}
		ProductDTO result = productService.save(productDTO);
		return ResponseEntity.created(new URI("/api/products/" + result.getId())).body(result);
	}

	/**
	 * {@code PUT  /products} : Updates an existing product.
	 *
	 * @param productDTO the productDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated productDTO, or with status {@code 400 (Bad Request)} if
	 *         the productDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the productDTO couldn't be
	 *         updated.
	 */
	@PutMapping("/products")
	public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO) {
		if (productDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id");
		}

		if (!productRepository.existsById(productDTO.getId())) {
			throw new BadRequestAlertException("Entity not found");
		}

		ProductDTO result = productService.save(productDTO);
		return ResponseEntity.ok().body(result);
	}

	/**
	 * {@code GET  /products} : get all the products.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of products in body.
	 */
	@GetMapping("/products")
	public List<ProductDTO> getAllProducts() {
		return productService.findAll();
	}

	/**
	 * {@code GET  /products/:id} : get the "id" product.
	 *
	 * @param id the id of the productDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the productDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/products/{id}")
	public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
		Optional<ProductDTO> productDTO = productService.findOne(id);
		return ResponseEntity.of(productDTO);
	}

	/**
	 * {@code DELETE  /products/:id} : delete the "id" product.
	 *
	 * @param id the id of the productDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
