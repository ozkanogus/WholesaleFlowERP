package tr.com.erpsample.grocery;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import tr.com.erpsample.grocery.initializer.DataPopulator;
import tr.com.erpsample.grocery.service.PurchaseService;
import tr.com.erpsample.grocery.web.rest.GroceryResource;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:context-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class GroceryContextTest {
    // Random demonstration data is outside this deterministic wiring test.
    @MockBean private DataPopulator dataPopulator;
    @Autowired private ApplicationContext context;

    @Test
    void applicationWiresServicesAndResources() {
        assertNotNull(context.getBean(PurchaseService.class));
        assertNotNull(context.getBean(GroceryResource.class));
    }
}
