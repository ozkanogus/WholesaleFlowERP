package tr.com.erpsample.grocery.web.rest;

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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import tr.com.erpsample.grocery.config.JacksonConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import tr.com.erpsample.grocery.repository.GroceryRepository;
import tr.com.erpsample.grocery.service.GroceryService;
import tr.com.erpsample.grocery.service.dto.GroceryDTO;
import tr.com.erpsample.grocery.web.rest.errors.ExceptionTranslator;

@ExtendWith(MockitoExtension.class)
class GroceryHttpTest {
    @Mock private GroceryService service;
    @Mock private GroceryRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void configureMvc() {
        JacksonConfiguration config = new JacksonConfiguration();
        ObjectMapper json = new ObjectMapper().registerModules(config.javaTimeModule(),
            config.jdk8TimeModule(), config.hibernate6Module(), config.problemModule(),
            config.constraintViolationProblemModule());
        mvc = MockMvcBuilders.standaloneSetup(new GroceryResource(service, repository))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(json))
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
