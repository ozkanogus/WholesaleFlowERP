package tr.com.erpsample.grocery;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tr.com.erpsample.grocery.initializer.DataPopulator;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "server.address=127.0.0.1",
    "spring.datasource.url=jdbc:h2:mem:webserver-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class WebServerTest {
    @MockitoBean(enforceOverride = true) private DataPopulator dataPopulator;
    @Autowired private ServletWebServerApplicationContext context;
    @Autowired private ServletWebServerFactory factory;

    @Test
    void tomcatServesRealHttpRequests() throws Exception {
        assertInstanceOf(TomcatServletWebServerFactory.class, factory);
        assertInstanceOf(TomcatWebServer.class, context.getWebServer());
        assertTrue(context.getWebServer().getPort() > 0);
        var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:"
            + context.getWebServer().getPort() + "/api/groceries/-1"))
            .timeout(Duration.ofSeconds(10)).GET().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("", response.body());
    }
}
