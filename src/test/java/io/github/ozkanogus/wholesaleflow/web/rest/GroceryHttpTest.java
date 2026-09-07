package io.github.ozkanogus.wholesaleflow.web.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;
import io.github.ozkanogus.wholesaleflow.config.JacksonConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.ozkanogus.wholesaleflow.repository.GroceryRepository;
import io.github.ozkanogus.wholesaleflow.service.GroceryService;
import io.github.ozkanogus.wholesaleflow.service.dto.GroceryDTO;
import io.github.ozkanogus.wholesaleflow.web.rest.errors.ExceptionTranslator;

@ExtendWith(MockitoExtension.class)
class GroceryHttpTest {
    @Mock private GroceryService service;
    @Mock private GroceryRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void configureMvc() {
        JacksonConfiguration config = new JacksonConfiguration();
        JsonMapper json = JsonMapper.builder().addModule(config.hibernate7Module()).build();
        mvc = MockMvcBuilders.standaloneSetup(new GroceryResource(service, repository))
            .setMessageConverters(new JacksonJsonHttpMessageConverter(json))
            .setControllerAdvice(new ExceptionTranslator()).build();
    }

    @Test
    void createSerializesSavedGroceryAndLocation() throws Exception {
        when(service.save(any(GroceryDTO.class)))
            .thenReturn(GroceryDTO.builder().id(42L).name("Market").build());
        mvc.perform(post("/api/groceries").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Market\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/groceries/42"))
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.name").value("Market"));
    }

    @Test
    void missingNameIsRejectedBeforeServiceInvocation() throws Exception {
        mvc.perform(post("/api/groceries").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void suppliedIdProducesBadRequestProblem() throws Exception {
        mvc.perform(post("/api/groceries").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":42,\"name\":\"Market\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(service);
    }

    @Test
    void getSerializesExistingGrocery() throws Exception {
        when(service.findOne(42L)).thenReturn(Optional.of(
            GroceryDTO.builder().id(42L).name("Market").build()));
        mvc.perform(get("/api/groceries/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Market"));
    }
}
