package tr.com.erpsample.grocery.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import tr.com.erpsample.grocery.domain.Grocery;
import tr.com.erpsample.grocery.domain.Product;
import tr.com.erpsample.grocery.domain.StockMovement;
import tr.com.erpsample.grocery.domain.enumeration.OperationType;
import tr.com.erpsample.grocery.domain.enumeration.Unit;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class PersistenceMappingTest {
    @Autowired private TestEntityManager entities;
    @Autowired private GroceryRepository groceries;
    @Autowired private StockMovementRepository movements;

    @Test
    void groceryCanBeFoundIgnoringNameCaseAfterReload() {
        Grocery grocery = entities.persistAndFlush(Grocery.builder().name("Wholesale Market").build());
        Long id = grocery.getId();
        entities.clear();

        assertEquals(id, groceries.findByNameIgnoreCase("WHOLESALE market").orElseThrow().getId());
    }

    @Test
    void stockMovementRetainsRelationshipsQuantityAndOperationType() {
        Grocery grocery = entities.persist(Grocery.builder().name("Market").build());
        Product product = entities.persist(Product.builder().name("Rice").unit(Unit.KG).build());
        StockMovement movement = new StockMovement();
        movement.setGrocery(grocery);
        movement.setProduct(product);
        movement.setOperationId(123L);
        movement.setOperationType(OperationType.SALE);
        movement.setCount(new BigDecimal("-2.50"));
        entities.persistAndFlush(movement);
        Long productId = product.getId();
        Long groceryId = grocery.getId();
        entities.clear();

        StockMovement loaded = movements.findByOperationIdAndProductIdAndOperationType(
            123L, productId, OperationType.SALE).orElseThrow();
        assertEquals(new BigDecimal("-2.50"), loaded.getCount());
        assertEquals(groceryId, loaded.getGrocery().getId());
        assertEquals(Unit.KG, loaded.getProduct().getUnit());
        assertTrue(movements.findByOperationIdAndProductIdAndOperationType(
            123L, productId, OperationType.PURCHASE).isEmpty());
    }
}
