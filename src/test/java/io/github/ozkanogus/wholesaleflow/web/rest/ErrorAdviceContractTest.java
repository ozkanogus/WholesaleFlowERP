package io.github.ozkanogus.wholesaleflow.web.rest;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;
import io.github.ozkanogus.wholesaleflow.web.rest.errors.ExceptionTranslator;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ErrorAdviceContractTest {
    @Test
    void preserveInheritedExceptionStatuses() throws Exception {
        var mapper = JsonMapper.builder().build();
        var http = MockMvcBuilders.standaloneSetup(new FailingController())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(mapper))
            .setControllerAdvice(new ExceptionTranslator()).build();
        for (String accept : new String[] {"application/json", "application/problem+json"}) {
            for (int code : new int[] {400, 501, 504}) {
                var response = http.perform(get("/test-only/status/" + code).accept(accept))
                    .andReturn().getResponse();
                var body = mapper.readTree(response.getContentAsString());
                org.junit.jupiter.api.Assertions.assertEquals(code, response.getStatus());
                org.junit.jupiter.api.Assertions.assertEquals(code, body.get("status").asInt());
                org.junit.jupiter.api.Assertions.assertEquals("synthetic diagnostic", body.get("detail").asText());
                org.junit.jupiter.api.Assertions.assertEquals("application/problem+json", response.getContentType());
                org.junit.jupiter.api.Assertions.assertEquals(3, body.size());
            }
        }
    }

    @Test
    void preserveRoutingErrors() throws Exception {
        var mapper = JsonMapper.builder().build();
        var http = MockMvcBuilders.standaloneSetup(new FailingController())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(mapper))
            .setControllerAdvice(new ExceptionTranslator()).build();
        for (String accept : new String[] {"application/json", "application/problem+json"}) {
            var requests = java.util.List.of(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/test-only/failure"),
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/test-only/body")
                    .contentType("text/plain").content("text"),
                get("/test-only/id/not-a-number"));
            int index = 0;
            for (var request : requests) {
                var response = http.perform(request.accept(accept)).andReturn().getResponse();
                var body = mapper.readTree(response.getContentAsString());
                int expected = new int[] {405, 415, 400}[index];
                org.junit.jupiter.api.Assertions.assertEquals(expected, response.getStatus());
                org.junit.jupiter.api.Assertions.assertEquals(expected, body.get("status").asInt());
                org.junit.jupiter.api.Assertions.assertEquals("application/problem+json", response.getContentType());
                org.junit.jupiter.api.Assertions.assertEquals(
                    new String[] {"Method Not Allowed", "Unsupported Media Type", "Bad Request"}[index],
                    body.get("title").asText());
                String prefix = new String[] {"Request method 'POST' is not supported",
                    "Content-Type 'text/plain' is not supported",
                    "Method parameter 'id': Failed to convert value"}[index];
                org.junit.jupiter.api.Assertions.assertTrue(body.get("detail").asText().startsWith(prefix));
                org.junit.jupiter.api.Assertions.assertEquals(3, body.size());
                if (index == 0) org.junit.jupiter.api.Assertions.assertEquals("GET", response.getHeader("Allow"));
                index++;
            }
        }
    }
    @Test
    void redactUnexpectedException() throws Exception {
        var mapper = JsonMapper.builder().build();
        var http = MockMvcBuilders.standaloneSetup(new FailingController())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(mapper))
            .setControllerAdvice(new ExceptionTranslator()).build();
        for (String accept : new String[] {"application/json", "application/problem+json"}) {
            var response = http.perform(get("/test-only/failure").accept(accept))
                .andExpect(status().isInternalServerError()).andReturn().getResponse();
            org.junit.jupiter.api.Assertions.assertEquals("application/problem+json", response.getContentType());
            org.junit.jupiter.api.Assertions.assertEquals(
                mapper.readTree("{\"title\":\"Internal Server Error\",\"status\":500}"),
                mapper.readTree(response.getContentAsString()));
        }
    }

    @RestController
    static class FailingController {
        @GetMapping("/test-only/status/{code}")
        String classified(@org.springframework.web.bind.annotation.PathVariable("code") int code)
                throws java.net.SocketTimeoutException {
            if (code == 400) throw new org.springframework.web.multipart.MultipartException("synthetic diagnostic");
            if (code == 501) throw new UnsupportedOperationException("synthetic diagnostic");
            throw new java.net.SocketTimeoutException("synthetic diagnostic");
        }

        @org.springframework.web.bind.annotation.PostMapping(value = "/test-only/body", consumes = "application/json")
        String body(@org.springframework.web.bind.annotation.RequestBody java.util.Map<String, String> body) {
            return "ok";
        }

        @GetMapping("/test-only/id/{id}")
        String id(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
            return id.toString();
        }

        @GetMapping("/test-only/failure")
        String fail() {
            // Synthetic diagnostic marker, not a real secret or infrastructure address.
            throw new IllegalStateException("internal-diagnostic-marker");
        }
    }
}
