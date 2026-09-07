package io.github.ozkanogus.wholesaleflow.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.ozkanogus.wholesaleflow.domain.Product;
import io.github.ozkanogus.wholesaleflow.repository.ProductRepository;
import io.github.ozkanogus.wholesaleflow.service.dto.ProductDTO;
import io.github.ozkanogus.wholesaleflow.service.mapper.ProductMapper;

/**
 * Service Implementation for managing {@link Product}.
 */
@Service
@Transactional
public class ProductService {

	private final ProductRepository productRepository;

	private final ProductMapper productMapper;

	public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
		this.productRepository = productRepository;
		this.productMapper = productMapper;
	}

	/**
	 * Save a product.
	 *
	 * @param productDTO the entity to save.
	 * @return the persisted entity.
	 */
	public ProductDTO save(ProductDTO productDTO) {
		Product product = productMapper.toEntity(productDTO);
		product = productRepository.save(product);
		return productMapper.toDto(product);
	}

	/**
	 * Get all the products.
	 *
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	public List<ProductDTO> findAll() {
		return productRepository.findAll().stream().map(productMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one product by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	public Optional<ProductDTO> findOne(Long id) {
		return productRepository.findById(id).map(productMapper::toDto);
	}

	/**
	 * Delete the product by id.
	 *
	 * @param id the id of the entity.
	 */
	public void delete(Long id) {
		productRepository.deleteById(id);
	}
}
