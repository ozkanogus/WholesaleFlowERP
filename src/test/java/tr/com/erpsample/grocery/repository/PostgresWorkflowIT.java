package tr.com.erpsample.grocery.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tr.com.erpsample.grocery.domain.Grocery;
import tr.com.erpsample.grocery.domain.Product;
import tr.com.erpsample.grocery.domain.enumeration.Unit;
import tr.com.erpsample.grocery.initializer.DataPopulator;
import tr.com.erpsample.grocery.service.PurchaseService;
import tr.com.erpsample.grocery.service.SaleService;
import tr.com.erpsample.grocery.service.dto.*;

@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL82Dialect"
})
@AutoConfigureMockMvc
class PostgresWorkflowIT {
    private int productNumber;
    @MockBean private DataPopulator demoData;
    @Autowired private PurchaseService purchases;
    @Autowired private SaleService sales;
    @Autowired private EntityManager entities;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private PlatformTransactionManager transactions;
    @Autowired private MockMvc http;

    @ParameterizedTest(name = "sale={0}: service-owned transaction rejects invalid foreign key atomically")
    @ValueSource(booleans = {false, true})
    void serviceOwnedTransactionRollsBackOnDatabaseFailure(boolean sale) {
        long product = new TransactionTemplate(transactions).execute(tx -> product());
        try {
            GroceryDTO missingGrocery = new GroceryDTO();
            missingGrocery.setId(-1L);
            assertEquals(0, count("select count(*) from grocery where id=-1"));
            // No test transaction surrounds this service call.
            RuntimeException failure = assertThrows(RuntimeException.class,
                () -> save(sale, null, missingGrocery, product, "2.50"));
            Throwable cause = failure;
            while (cause.getCause() != null) cause = cause.getCause();
            assertInstanceOf(java.sql.SQLException.class, cause);
            assertEquals("23503", ((java.sql.SQLException) cause).getSQLState());
            assertEquals(0, count("select count(*) from " + table(sale) + " where grocery_id=-1"));
            assertEquals(0, count("select count(*) from " + table(sale) + "_product where product_id=?", product));
            assertEquals(0, count("select count(*) from stock_movement where product_id=?", product));
        } finally {
            // Remove only the exact prerequisite row created by this test.
            new TransactionTemplate(transactions).executeWithoutResult(tx ->
                jdbc.update("delete from product where id=?", product));
        }
    }

    @ParameterizedTest(name = "sale={0}: full-context error contracts")
    @ValueSource(booleans = {false, true})
    void httpErrorContracts(boolean sale) throws Exception {
        String path = "/api/" + (sale ? "sales" : "purchases");
        http.perform(post(path).contentType(MediaType.APPLICATION_JSON).content("{\"id\":-1}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
            .andExpect(jsonPath("$.status").value(400));
        http.perform(put(path).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        http.perform(get(path + "/-1"))
            .andExpect(status().isNotFound()).andExpect(content().string(""));
        http.perform(post(path).contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @ParameterizedTest(name = "sale={0}: full-context DTO JSON reload")
    @ValueSource(booleans = {false, true})
    void httpReadsPersistedDto(boolean sale) {
        new TransactionTemplate(transactions).executeWithoutResult(tx -> {
            try {
                GroceryDTO grocery = grocery();
                long product = product();
                long id = save(sale, null, grocery, product, "2.50");
                flushAndClear();
                http.perform(get("/api/" + (sale ? "sales" : "purchases") + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.grocery.id").value(grocery.getId()))
                    .andExpect(jsonPath("$.products.length()").value(1))
                    .andExpect(jsonPath("$.products[0].productId").value(product))
                    .andExpect(jsonPath("$.products[0].count").value(2.5))
                    .andExpect(jsonPath("$.products[0].price").value(3.75))
                    .andExpect(jsonPath("$.createdDate").value("2026-01-15T12:00:00Z"));
            } catch (Exception exception) {
                throw new AssertionError(exception);
            } finally { tx.setRollbackOnly(); }
        });
    }

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry properties) {
        PostgresReportIT.database(properties);
    }

    @ParameterizedTest(name = "sale={0}: persists lines and stock, then cascades deletion")
    @ValueSource(booleans = {false, true})
    void persistsAndDeletes(boolean sale) {
        new TransactionTemplate(transactions).executeWithoutResult(tx -> {
            try {
                GroceryDTO grocery = grocery();
                long product = product();
                long id = save(sale, null, grocery, product, "2.50");
                flushAndClear();
                assertRows(sale, id, product, "2.50");
                if (sale) sales.delete(id); else purchases.delete(id);
                flushAndClear();
                assertEquals(0, count("select count(*) from " + table(sale) + " where id=?", id));
                assertEquals(0, count("select count(*) from " + table(sale) + "_product where " + table(sale) + "_id=?", id));
                assertEquals(0, count("select count(*) from stock_movement where operation_id=?", id));
                assertEquals(1, count("select count(*) from product where id=?", product));
                assertEquals(1, count("select count(*) from grocery where id=?", grocery.getId()));
            } finally { tx.setRollbackOnly(); }
        });
    }

    @ParameterizedTest(name = "sale={0}: replaces old line and stock")
    @ValueSource(booleans = {false, true})
    void replacesLinesAndStock(boolean sale) {
        new TransactionTemplate(transactions).executeWithoutResult(tx -> {
            try {
                GroceryDTO grocery = grocery();
                long original = product(), replacement = product();
                long id = save(sale, null, grocery, original, "2.50");
                flushAndClear();
                assertEquals(id, save(sale, id, grocery, replacement, "7.25"));
                flushAndClear();
                assertRows(sale, id, replacement, "7.25");
                assertEquals(0, count("select count(*) from stock_movement where operation_id=? and product_id=?", id, original));
            } finally { tx.setRollbackOnly(); }
        });
    }

    @ParameterizedTest(name = "sale={0}: rolls back flushed aggregate and stock")
    @ValueSource(booleans = {false, true})
    void rollsBackOnDownstreamFailure(boolean sale) {
        long[] ids = new long[2];
        assertThrows(ForcedFailure.class, () -> new TransactionTemplate(transactions).executeWithoutResult(tx -> {
            GroceryDTO grocery = grocery();
            ids[0] = grocery.getId();
            ids[1] = save(sale, null, grocery, product(), "2.50");
            flushAndClear();
            assertEquals(1, count("select count(*) from stock_movement where operation_id=?", ids[1]));
            throw new ForcedFailure();
        }));
        assertTrue(ids[1] > 0);
        assertEquals(0, count("select count(*) from " + table(sale) + " where id=?", ids[1]));
        assertEquals(0, count("select count(*) from " + table(sale) + "_product where " + table(sale) + "_id=?", ids[1]));
        assertEquals(0, count("select count(*) from stock_movement where operation_id=?", ids[1]));
        assertEquals(0, count("select count(*) from grocery where id=?", ids[0]));
    }

    private void assertRows(boolean sale, long id, long product, String quantity) {
        String table = table(sale);
        assertEquals(1, count("select count(*) from " + table + "_product where " + table + "_id=?", id));
        assertEquals(new BigDecimal(quantity), jdbc.queryForObject("select count from " + table + "_product where " + table + "_id=? and product_id=?", BigDecimal.class, id, product));
        assertEquals(new BigDecimal("3.75"), jdbc.queryForObject("select price from " + table + "_product where " + table + "_id=?", BigDecimal.class, id));
        assertEquals(1, count("select count(*) from stock_movement where operation_id=?", id));
        assertEquals(new BigDecimal(quantity).multiply(BigDecimal.valueOf(sale ? -1 : 1)),
            jdbc.queryForObject("select count from stock_movement where operation_id=? and product_id=? and operation_type=?", BigDecimal.class, id, product, sale ? "SALE" : "PURCHASE"));
        Set<ProductSalePurchaseDTO> lines = sale ? sales.findOne(id).orElseThrow().getProducts() : purchases.findOne(id).orElseThrow().getProducts();
        assertEquals(1, lines.size());
        assertEquals(product, lines.iterator().next().getProductId());
        assertEquals(new BigDecimal(quantity), lines.iterator().next().getCount());
    }

    private long save(boolean sale, Long id, GroceryDTO grocery, long product, String quantity) {
        ProductSalePurchaseDTO line = new ProductSalePurchaseDTO();
        line.setProductId(product); line.setCount(new BigDecimal(quantity)); line.setPrice(new BigDecimal("3.75"));
        Instant date = Instant.parse("2026-01-15T12:00:00Z");
        if (sale) {
            SaleDTO dto = new SaleDTO();
            dto.setId(id); dto.setGrocery(grocery); dto.setProducts(Set.of(line));
            dto.setCreatedDate(date); dto.setLastModifiedDate(date);
            return sales.save(dto).getId();
        }
        PurchaseDTO dto = new PurchaseDTO();
        dto.setId(id); dto.setGrocery(grocery); dto.setProducts(Set.of(line));
        dto.setCreatedDate(date); dto.setLastModifiedDate(date);
        return purchases.save(dto).getId();
    }

    private GroceryDTO grocery() {
        Grocery entity = Grocery.builder().name("Workflow market").build();
        entities.persist(entity);
        GroceryDTO dto = new GroceryDTO(); dto.setId(entity.getId());
        return dto;
    }

    private long product() {
        Product entity = Product.builder().name("Workflow product " + ++productNumber).unit(Unit.KG).build();
        entities.persist(entity);
        return entity.getId();
    }

    private String table(boolean sale) { return sale ? "sale" : "purchase"; }
    private int count(String query, Object... args) { return jdbc.queryForObject(query, Integer.class, args); }
    private void flushAndClear() { entities.flush(); entities.clear(); }
    private static class ForcedFailure extends RuntimeException { }
}
