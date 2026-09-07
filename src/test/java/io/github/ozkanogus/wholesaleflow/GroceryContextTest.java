package io.github.ozkanogus.wholesaleflow;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockingDetails;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import io.github.ozkanogus.wholesaleflow.initializer.DataPopulator;
import io.github.ozkanogus.wholesaleflow.service.PurchaseService;
import io.github.ozkanogus.wholesaleflow.web.rest.GroceryResource;

@SpringBootTest(properties = {
    "grocery.demo-data.enabled=true",
    "spring.datasource.url=jdbc:h2:mem:context-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class GroceryContextTest {
    // Require replacement of the real bean, never silently add a missing mock.
    @MockitoBean(enforceOverride = true) private DataPopulator dataPopulator;
    @Autowired private ApplicationContext context;
    @Autowired private MockMvc http;
    @Autowired private JdbcTemplate jdbc;

    @Test
    void applicationWiresServicesAndResources() {
        assertNotNull(context.getBean(PurchaseService.class));
        assertNotNull(context.getBean(GroceryResource.class));
        assertSame(dataPopulator, context.getBean(DataPopulator.class));
        assertTrue(mockingDetails(dataPopulator).isMock());
        assertEquals(0, jdbc.queryForObject("select count(*) from grocery", Integer.class));
    }

    @Test
    void mvcUsesJackson3WithHibernate7Module() {
        var mapper = context.getBean(tools.jackson.databind.json.JsonMapper.class);
        assertTrue(mapper.registeredModules().stream()
            .anyMatch(tools.jackson.datatype.hibernate7.Hibernate7Module.class::isInstance));
        assertTrue(context.getBeansOfType(com.fasterxml.jackson.databind.ObjectMapper.class).isEmpty());
        var adapter = context.getBean(
            org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.class);
        var converters = adapter.getMessageConverters().stream()
            .filter(org.springframework.http.converter.json.JacksonJsonHttpMessageConverter.class::isInstance)
            .map(org.springframework.http.converter.json.JacksonJsonHttpMessageConverter.class::cast)
            .toList();
        assertFalse(converters.isEmpty());
        assertTrue(converters.stream().anyMatch(converter -> converter.getMapper() == mapper));
    }

    @Test
    void suppliedIdCreateRetainsCustomProblemTitle() throws Exception {
        customProblem(post("/api/groceries").content("{\"id\":-1,\"name\":\"Contract market\"}"),
            "A new grocery cannot already have an ID");
    }

    @Test
    void updateWithoutIdRetainsCustomProblemTitle() throws Exception {
        customProblem(put("/api/groceries").content("{\"name\":\"Contract market\"}"), "Invalid id");
    }

    @Test
    void updateUnknownIdRetainsCustomProblemTitle() throws Exception {
        assertEquals(0, jdbc.queryForObject("select count(*) from grocery where id=-1", Integer.class));
        customProblem(put("/api/groceries").content("{\"id\":-1,\"name\":\"Contract market\"}"),
            "Entity not found");
    }

    @Test
    void malformedJsonRetainsProblemDetail() throws Exception {
        problem(post("/api/groceries").content("{"))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            // Preserve the stable explanation, not Jackson's source-location formatting.
            .andExpect(jsonPath("$.detail").value(startsWith("JSON parse error: Unexpected end-of-input")))
            .andExpect(jsonPath("$.type").doesNotHaveJsonPath())
            .andExpect(jsonPath("$.violations").doesNotHaveJsonPath());
    }

    @Test
    void validationRetainsEveryViolationAndProblemType() throws Exception {
        problem(post("/api/stockMovements").content("{}"))
            .andExpect(jsonPath("$.title").value("Constraint Violation"))
            .andExpect(jsonPath("$.type").value("https://zalando.github.io/problem/constraint-violation"))
            .andExpect(jsonPath("$.detail").doesNotHaveJsonPath())
            .andExpect(jsonPath("$.violations", hasSize(5)))
            .andExpect(jsonPath("$.violations[*].field", containsInAnyOrder(
                "count", "grocery", "operationId", "operationType", "product")))
            .andExpect(jsonPath("$.violations[*].message", everyItem(is("must not be null"))));
    }

    private ResultActions problem(MockHttpServletRequestBuilder request) throws Exception {
        return http.perform(request.contentType("application/json").header("Accept-Language", "en"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/problem+json"))
            .andExpect(jsonPath("$.status").value(400));
    }

    private void customProblem(MockHttpServletRequestBuilder request, String title) throws Exception {
        problem(request).andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.type").doesNotHaveJsonPath())
            .andExpect(jsonPath("$.detail").doesNotHaveJsonPath())
            .andExpect(jsonPath("$.violations").doesNotHaveJsonPath());
        assertEquals(0, jdbc.queryForObject("select count(*) from grocery", Integer.class));
    }
}
