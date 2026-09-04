package tr.com.erpsample.grocery.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tr.com.erpsample.grocery.domain.Grocery;
import tr.com.erpsample.grocery.domain.Product;
import tr.com.erpsample.grocery.domain.Purchase;
import tr.com.erpsample.grocery.domain.PurchaseProduct;
import tr.com.erpsample.grocery.domain.Sale;
import tr.com.erpsample.grocery.domain.SaleProduct;
import tr.com.erpsample.grocery.domain.StockMovement;
import tr.com.erpsample.grocery.domain.enumeration.OperationType;
import tr.com.erpsample.grocery.repository.StockMovementRepository;
import tr.com.erpsample.grocery.service.mapper.StockMovementMapper;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

	@Mock
	private StockMovementRepository repository;

	@Mock
	private StockMovementMapper mapper;

	@InjectMocks
	private StockMovementService service;

	@Test
	void purchaseCreatesPositiveStockMovementAfterReplacingPreviousMovement() {
		Grocery grocery = Grocery.builder().id(10L).name("Market").build();
		Product product = Product.builder().id(20L).name("Rice").build();
		PurchaseProduct line = new PurchaseProduct().product(product).count(new BigDecimal("12.50"));
		Purchase purchase = new Purchase();
		purchase.setId(30L);
		purchase.setGrocery(grocery);
		purchase.setProducts(Set.of(line));

		service.updateStockMovement(OperationType.PURCHASE, purchase);

		ArgumentCaptor<StockMovement> movement = ArgumentCaptor.forClass(StockMovement.class);
		InOrder calls = inOrder(repository);
		calls.verify(repository).deleteByOperationId(30L);
		calls.verify(repository).save(movement.capture());
		assertEquals(new BigDecimal("12.50"), movement.getValue().getCount());
		assertEquals(OperationType.PURCHASE, movement.getValue().getOperationType());
		assertEquals(30L, movement.getValue().getOperationId());
		assertSame(grocery, movement.getValue().getGrocery());
		assertSame(product, movement.getValue().getProduct());
	}

	@Test
	void saleCreatesNegativeStockMovementAfterReplacingPreviousMovement() {
		Grocery grocery = Grocery.builder().id(11L).name("Market").build();
		Product product = Product.builder().id(21L).name("Oil").build();
		SaleProduct line = new SaleProduct().product(product).count(new BigDecimal("3.25"));
		Sale sale = new Sale();
		sale.setId(31L);
		sale.setGrocery(grocery);
		sale.setProducts(Set.of(line));

		service.updateStockMovement(OperationType.SALE, sale);

		ArgumentCaptor<StockMovement> movement = ArgumentCaptor.forClass(StockMovement.class);
		InOrder calls = inOrder(repository);
		calls.verify(repository).deleteByOperationId(31L);
		calls.verify(repository).save(movement.capture());
		assertEquals(new BigDecimal("-3.25"), movement.getValue().getCount());
		assertEquals(OperationType.SALE, movement.getValue().getOperationType());
		assertEquals(31L, movement.getValue().getOperationId());
		assertSame(grocery, movement.getValue().getGrocery());
		assertSame(product, movement.getValue().getProduct());
	}

	@Test
	void unsupportedOperationTypeDoesNotAlterStockMovements() {
		service.updateStockMovement(OperationType.INVENTORY, new Object());

		verifyNoMoreInteractions(repository);
	}
}
