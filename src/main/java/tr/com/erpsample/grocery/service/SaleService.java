package tr.com.erpsample.grocery.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tr.com.erpsample.grocery.domain.Product;
import tr.com.erpsample.grocery.domain.Sale;
import tr.com.erpsample.grocery.domain.SaleProduct;
import tr.com.erpsample.grocery.domain.enumeration.OperationType;
import tr.com.erpsample.grocery.repository.ProductRepository;
import tr.com.erpsample.grocery.repository.SaleRepository;
import tr.com.erpsample.grocery.service.dto.ProductDTO;
import tr.com.erpsample.grocery.service.dto.SaleDTO;
import tr.com.erpsample.grocery.service.mapper.ProductMapper;
import tr.com.erpsample.grocery.service.mapper.SaleMapper;

/**
 * Service Implementation for managing {@link Sale}.
 */
@Service
@Transactional
public class SaleService {

	private final SaleRepository saleRepository;

	private final SaleMapper saleMapper;

	private final ProductRepository productRepository;

	@Autowired
	private ProductMapper productMapper;

	private StockMovementService stockMovementService;

	public SaleService(SaleRepository saleRepository, SaleMapper saleMapper, ProductRepository productRepository,
			StockMovementService stockMovementService) {
		this.saleRepository = saleRepository;
		this.saleMapper = saleMapper;
		this.productRepository = productRepository;
		this.stockMovementService = stockMovementService;
	}

	/**
	 * Save a sale.
	 *
	 * @param saleDTO the entity to save.
	 * @return the persisted entity.
	 */
	public SaleDTO save(SaleDTO saleDTO) {
		Sale sale = saleMapper.toEntity(saleDTO);
		final Set<SaleProduct> products = saleDTO.getProducts().stream()
				.map(product -> new SaleProduct()
						.product(productRepository.findById(product.getProductId()).orElse(null))
						.count(product.getCount()).price(product.getPrice()))
				.collect(Collectors.toSet());
		sale.setProducts(products);
		Sale saleSaved = saleRepository.save(sale);
		// StockMovement
		stockMovementService.updateStockMovement(OperationType.SALE, saleSaved);
		//
		return saleMapper.toDto(saleSaved);

	}

	/**
	 * Get all the sales.
	 *
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	public List<SaleDTO> findAll() {
		return saleRepository.findAll().stream().map(saleMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one sale by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	public Optional<SaleDTO> findOne(Long id) {
		return saleRepository.findById(id).map(saleMapper::toDto);
	}

	/**
	 * Delete the sale by id.
	 *
	 * @param id the id of the entity.
	 */
	public void delete(Long id) {
		// StockMovement
		stockMovementService.deleteByOperationId(id);
		//
		saleRepository.deleteById(id);
	}

	/**
	 * Get 3 sales by groceryId.
	 *
	 * @param groceryId the id of the Grocery.
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	public List<ProductDTO> findTopSoldThreeProduct(Long groceryId) {
		List<Long> productIdList = saleRepository.findTopSoldThreeProduct(groceryId);
		List<Product> productList = new ArrayList<Product>();
		productIdList.forEach(id -> {
			Optional<Product> optional = productRepository.findById(id);
			if (optional.isPresent())
				productList.add(optional.orElseThrow());
		});
		return productMapper.toDto(productList);
	}
}
