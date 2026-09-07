package io.github.ozkanogus.wholesaleflow.web.rest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import io.github.ozkanogus.wholesaleflow.repository.*;
import io.github.ozkanogus.wholesaleflow.service.*;
import io.github.ozkanogus.wholesaleflow.service.dto.*;

class ResourceLookupHttpTest {

    @Test
    void groceriesDistinguishMissingAndExistingRecords() throws Exception {
        GroceryService service = mock(GroceryService.class);
        GroceryDTO dto = new GroceryDTO();
        dto.setId(42L);
        when(service.findOne(42L)).thenReturn(Optional.of(dto));
        when(service.findOne(99L)).thenReturn(Optional.empty());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new GroceryResource(service, mock(GroceryRepository.class))).build();

        mvc.perform(get("/api/groceries/42"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(42));
        mvc.perform(get("/api/groceries/99"))
            .andExpect(status().isNotFound()).andExpect(content().string(""));
    }

    @Test
    void productsDistinguishMissingAndExistingRecords() throws Exception {
        ProductService service = mock(ProductService.class);
        ProductDTO dto = new ProductDTO();
        dto.setId(42L);
        when(service.findOne(42L)).thenReturn(Optional.of(dto));
        when(service.findOne(99L)).thenReturn(Optional.empty());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new ProductResource(service, mock(ProductRepository.class))).build();

        mvc.perform(get("/api/products/42"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(42));
        mvc.perform(get("/api/products/99"))
            .andExpect(status().isNotFound()).andExpect(content().string(""));
    }

    @Test
    void purchasesDistinguishMissingAndExistingRecords() throws Exception {
        PurchaseService service = mock(PurchaseService.class);
        PurchaseDTO dto = new PurchaseDTO();
        dto.setId(42L);
        when(service.findOne(42L)).thenReturn(Optional.of(dto));
        when(service.findOne(99L)).thenReturn(Optional.empty());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new PurchaseResource(service, mock(PurchaseRepository.class))).build();

        mvc.perform(get("/api/purchases/42"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(42));
        mvc.perform(get("/api/purchases/99"))
            .andExpect(status().isNotFound()).andExpect(content().string(""));
    }

    @Test
    void salesDistinguishMissingAndExistingRecords() throws Exception {
        SaleService service = mock(SaleService.class);
        SaleDTO dto = new SaleDTO();
        dto.setId(42L);
        when(service.findOne(42L)).thenReturn(Optional.of(dto));
        when(service.findOne(99L)).thenReturn(Optional.empty());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new SaleResource(service, mock(SaleRepository.class))).build();

        mvc.perform(get("/api/sales/42"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(42));
        mvc.perform(get("/api/sales/99"))
            .andExpect(status().isNotFound()).andExpect(content().string(""));
    }

    @Test
    void stockMovementsDistinguishMissingAndExistingRecords() throws Exception {
        StockMovementService service = mock(StockMovementService.class);
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(42L);
        when(service.findOne(42L)).thenReturn(Optional.of(dto));
        when(service.findOne(99L)).thenReturn(Optional.empty());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new StockMovementResource(service, mock(StockMovementRepository.class))).build();

        mvc.perform(get("/api/stockMovements/42"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(42));
        mvc.perform(get("/api/stockMovements/99"))
            .andExpect(status().isNotFound()).andExpect(content().string(""));
    }
}
