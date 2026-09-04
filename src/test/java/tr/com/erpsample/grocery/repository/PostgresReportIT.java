package tr.com.erpsample.grocery.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Native SQL fixtures intentionally isolate reporting from service/cascade behavior. */
@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostgresReportIT {
    @Autowired private JdbcTemplate jdbc;
    @Autowired private SaleRepository sales;

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry properties) {
        String url = required("GROCERY_TEST_DB_URL");
        if (!url.matches("jdbc:postgresql://[^/]+/[a-zA-Z0-9_]+_test")) {
            throw new IllegalArgumentException("Use a dedicated PostgreSQL database ending in _test");
        }
        properties.add("spring.datasource.url", () -> url);
        properties.add("spring.datasource.username", () -> required("GROCERY_TEST_DB_USER"));
        properties.add("spring.datasource.password", () -> required("GROCERY_TEST_DB_PASSWORD"));
        properties.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing " + name);
        return value;
    }

    @Test
    void ranksBySummedQuantityNotPriceAndLimitsToThree() {
        long grocery = grocery();
        long first = product(), second = product(), third = product(), fourth = product();
        sale(grocery, first, 6, "now()", 1);
        sale(grocery, first, 6, "now()", 1);
        sale(grocery, second, 10, "now()", 999);
        sale(grocery, third, 8, "now()", 1);
        sale(grocery, fourth, 1, "now()", 1);
        assertEquals(List.of(first, second, third), sales.findTopSoldThreeProduct(grocery));
    }

    @Test
    void includesMonthEdgesAndExcludesAdjacentMonthsAndOtherGroceries() {
        long grocery = grocery(), other = grocery();
        long first = product(), last = product(), excluded = product();
        sale(grocery, first, 3, "date_trunc('month', now())", 1);
        sale(grocery, last, 2, "date_trunc('month', now()) + interval '1 month' - interval '1 microsecond'", 1);
        sale(grocery, excluded, 100, "date_trunc('month', now()) - interval '1 microsecond'", 1);
        sale(grocery, excluded, 100, "date_trunc('month', now()) + interval '1 month'", 1);
        sale(other, excluded, 100, "now()", 1);
        assertEquals(List.of(first, last), sales.findTopSoldThreeProduct(grocery));
        assertEquals(List.of(excluded), sales.findTopSoldThreeProduct(other));
    }

    @Test
    void returnsEmptyWhenGroceryHasNoSales() {
        assertEquals(List.of(), sales.findTopSoldThreeProduct(grocery()));
    }

    @Test
    void breaksQuantityTiesByProductIdBeforeApplyingLimit() {
        long grocery = grocery();
        long first = product(), second = product(), third = product(), fourth = product();
        sale(grocery, fourth, 5, "now()", 1);
        sale(grocery, third, 5, "now()", 1);
        sale(grocery, second, 5, "now()", 1);
        sale(grocery, first, 5, "now()", 1);
        assertEquals(List.of(first, second, third), sales.findTopSoldThreeProduct(grocery));
    }

    private long nextId() {
        return jdbc.queryForObject("select nextval('sequence_generator')", Long.class);
    }

    private long grocery() {
        long id = nextId();
        jdbc.update("insert into grocery(id,name) values (?,?)", id, "Report market " + id);
        return id;
    }

    private long product() {
        long id = nextId();
        jdbc.update("insert into product(id,name,unit) values (?,?,?)", id, "Report product " + id, "KG");
        return id;
    }

    private void sale(long grocery, long product, int count, String dateExpression, int price) {
        long id = nextId();
        // Expressions are fixed test literals; PostgreSQL now() is stable within the test transaction.
        jdbc.update("insert into sale(id,grocery_id,created_date) values (?, ?, " + dateExpression + ")", id, grocery);
        jdbc.update("insert into sale_product(sale_id,product_id,count,price) values (?,?,?,?)", id, product, count, price);
    }
}
