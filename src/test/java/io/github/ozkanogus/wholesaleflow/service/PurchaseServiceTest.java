package io.github.ozkanogus.wholesaleflow.service;

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

import io.github.ozkanogus.wholesaleflow.domain.Grocery;
import io.github.ozkanogus.wholesaleflow.domain.Purchase;
import io.github.ozkanogus.wholesaleflow.domain.PurchaseProduct;
import io.github.ozkanogus.wholesaleflow.domain.PurchaseProductId;
import io.github.ozkanogus.wholesaleflow.domain.enumeration.OperationType;
import io.github.ozkanogus.wholesaleflow.repository.ProductRepository;
import io.github.ozkanogus.wholesaleflow.repository.PurchaseRepository;
import io.github.ozkanogus.wholesaleflow.service.dto.GroceryDTO;
import io.github.ozkanogus.wholesaleflow.service.dto.ProductSalePurchaseDTO;
import io.github.ozkanogus.wholesaleflow.service.dto.PurchaseDTO;
import io.github.ozkanogus.wholesaleflow.service.mapper.PurchaseMapper;

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
