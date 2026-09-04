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
import tr.com.erpsample.grocery.domain.Sale;
import tr.com.erpsample.grocery.domain.SaleProduct;
import tr.com.erpsample.grocery.domain.SaleProductId;
import tr.com.erpsample.grocery.domain.enumeration.OperationType;
import tr.com.erpsample.grocery.repository.ProductRepository;
import tr.com.erpsample.grocery.repository.SaleRepository;
import tr.com.erpsample.grocery.service.dto.GroceryDTO;
import tr.com.erpsample.grocery.service.dto.ProductSalePurchaseDTO;
import tr.com.erpsample.grocery.service.dto.SaleDTO;
import tr.com.erpsample.grocery.service.mapper.SaleMapper;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

	@InjectMocks
	private SaleService service;

	@Mock
	private SaleRepository repository;

	@Mock
	private SaleMapper mapper;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private StockMovementService stockMovementService;

	@Test
	void givenSaleDto_thenSave_verifyStock() {

		// given
		SaleDTO dto = newTestDto();

		Sale entity = newTestEntity();

		// when
		when(mapper.toEntity(dto)).thenReturn(entity);
		when(repository.save(entity)).thenReturn(entity);
		when(mapper.toDto(entity)).thenReturn(dto);

		// then
		SaleDTO result = service.save(dto);
		verify(stockMovementService).updateStockMovement(OperationType.SALE, mapper.toEntity(result));
	}


	@Test
	void givenSaleDto_thenSave_expectGivenDto() {

		// given
		SaleDTO dto = newTestDto();

		Sale entity = newTestEntity();

		// when
		when(mapper.toEntity(dto)).thenReturn(entity);
		when(repository.save(entity)).thenReturn(entity);
		when(mapper.toDto(entity)).thenReturn(dto);

		// then
		SaleDTO result = service.save(dto);

		assertEquals(dto, result);
	}



	@Test
	void givenSaleId_thenFindOne_expectDto() {

		// given
		Long saleId = -1L;
		SaleDTO dto = newTestDto();

		Sale entity = newTestEntity();

		Optional<SaleDTO> optionalDto = Optional.ofNullable(dto);

		// when
		when(mapper.toDto(entity)).thenReturn(dto);
		when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

		// then
		Optional<SaleDTO> result = service.findOne(saleId);

		assertEquals(optionalDto, result);
	}
	
	private Sale newTestEntity() {
		Sale sale = new Sale();
		sale.setId(-1L);
		sale.setGrocery(Grocery.builder().id(-1L).name("Test Grocery Entity").build());

		Set<SaleProduct> products = new HashSet<SaleProduct>();
		SaleProduct saleProduct = new SaleProduct();
		saleProduct.setId(new SaleProductId(-1L, -1L));
		products.add(saleProduct);
		sale.setProducts(products);
		return sale;
	}

	private SaleDTO newTestDto() {
		SaleDTO saleDTO = new SaleDTO();
		saleDTO.setId(-1L);
		saleDTO.setGrocery(GroceryDTO.builder().id(-1L).name("Test Grocery DTO").build());

		Set<ProductSalePurchaseDTO> products = new HashSet<ProductSalePurchaseDTO>();
		ProductSalePurchaseDTO salePurchaseDTO = new ProductSalePurchaseDTO();
		salePurchaseDTO.setCount(BigDecimal.TEN);
		salePurchaseDTO.setPrice(BigDecimal.ONE);
		salePurchaseDTO.setProductId(-1L);
		products.add(salePurchaseDTO);

		saleDTO.setProducts(products);
		return saleDTO;
	}
	
}
