package tr.com.erpsample.grocery.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tr.com.erpsample.grocery.domain.Grocery;
import tr.com.erpsample.grocery.domain.Purchase;
import tr.com.erpsample.grocery.domain.PurchaseProduct;
import tr.com.erpsample.grocery.domain.PurchaseProductId;
import tr.com.erpsample.grocery.domain.enumeration.OperationType;
import tr.com.erpsample.grocery.repository.ProductRepository;
import tr.com.erpsample.grocery.repository.PurchaseRepository;
import tr.com.erpsample.grocery.service.dto.GroceryDTO;
import tr.com.erpsample.grocery.service.dto.ProductSalePurchaseDTO;
import tr.com.erpsample.grocery.service.dto.PurchaseDTO;
import tr.com.erpsample.grocery.service.mapper.PurchaseMapper;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

	@InjectMocks
	private PurchaseService service;

	@Mock
	private PurchaseRepository repository;

	@Mock
	private PurchaseMapper mapper;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private StockMovementService stockMovementService;

	@Test
	void givenPurchaseDto_thenSave_verifyStock() {

		// given
		PurchaseDTO dto = newTestDto();

		Purchase entity = newTestEntity();

		// when
		when(mapper.toEntity(dto)).thenReturn(entity);
		when(repository.save(entity)).thenReturn(entity);
		when(mapper.toDto(entity)).thenReturn(dto);

		// then
		PurchaseDTO result = service.save(dto);
		verify(stockMovementService).updateStockMovement(OperationType.PURCHASE, mapper.toEntity(result));
	}

	@Test
	void givenPurchaseDto_thenSave_expectGivenDto() {

		// given
		PurchaseDTO dto = newTestDto();

		Purchase entity = newTestEntity();

		// when
		when(mapper.toEntity(dto)).thenReturn(entity);
		when(repository.save(entity)).thenReturn(entity);
		when(mapper.toDto(entity)).thenReturn(dto);

		// then
		PurchaseDTO result = service.save(dto);

		assertEquals(dto, result);
	}



	@Test
	void givenPurchaseId_thenFindOne_expectDto() {

		// given
		Long purchaseId = -1L;
		PurchaseDTO dto = newTestDto();

		Purchase entity = newTestEntity();

		Optional<PurchaseDTO> optionalDto = Optional.ofNullable(dto);

		// when
		when(mapper.toDto(entity)).thenReturn(dto);
		when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

		// then
		Optional<PurchaseDTO> result = service.findOne(purchaseId);

		assertEquals(optionalDto, result);
	}
	
	private Purchase newTestEntity() {
		Purchase purchase = new Purchase();
		purchase.setId(-1L);
		purchase.setGrocery(Grocery.builder().id(-1L).name("Test Grocery Entity").build());

		Set<PurchaseProduct> products = new HashSet<PurchaseProduct>();
		PurchaseProduct purchaseProduct = new PurchaseProduct();
		purchaseProduct.setId(new PurchaseProductId(-1L, -1L));
		products.add(purchaseProduct);
		purchase.setProducts(products);
		return purchase;
	}

	private PurchaseDTO newTestDto() {
		PurchaseDTO purchaseDTO = new PurchaseDTO();
		purchaseDTO.setId(-1L);
		purchaseDTO.setGrocery(GroceryDTO.builder().id(-1L).name("Test Grocery DTO").build());

		Set<ProductSalePurchaseDTO> products = new HashSet<ProductSalePurchaseDTO>();
		ProductSalePurchaseDTO purchasePurchaseDTO = new ProductSalePurchaseDTO();
		purchasePurchaseDTO.setCount(BigDecimal.TEN);
		purchasePurchaseDTO.setPrice(BigDecimal.ONE);
		purchasePurchaseDTO.setProductId(-1L);
		products.add(purchasePurchaseDTO);

		purchaseDTO.setProducts(products);
		return purchaseDTO;
	}
	
}
