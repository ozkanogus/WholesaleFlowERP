package tr.com.erpsample.grocery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.Properties;
import org.junit.jupiter.api.Test;

/** Runs in Maven verify after Spring Boot has repackaged the artifact. */
class PackagedApplicationIT {
    @Test
    void packagedRuntimeHasNoBundledDatabaseConnectionOrDemoOptIn() throws Exception {
        try (JarFile jar = new JarFile(System.getProperty("packagedApplicationJar"))) {
            Properties properties = new Properties();
            try (var input = jar.getInputStream(jar.getJarEntry(
                    "BOOT-INF/classes/config/application.properties"))) {
                properties.load(input);
            }
            for (String key : new String[] {"spring.datasource.url", "spring.datasource.username",
                    "spring.datasource.password", "grocery.demo-data.enabled"}) {
                assertFalse(properties.containsKey(key), key + " must be supplied externally");
            }
            assertEquals("validate", properties.getProperty("spring.jpa.hibernate.ddl-auto"));
            assertEquals("false", properties.getProperty("spring.flyway.baseline-on-migrate"));
            assertEquals("true", properties.getProperty("spring.flyway.clean-disabled"));
            assertEquals("true", properties.getProperty("spring.flyway.validate-on-migrate"));
            assertNotNull(jar.getJarEntry(
                "BOOT-INF/classes/db/migration/V1__initial_schema.sql"));
        }
    }

    @Test
    void executableJarPointsToItsPackagedApplicationClass() throws Exception {
        try (JarFile jar = new JarFile(System.getProperty("packagedApplicationJar"))) {
            Attributes manifest = jar.getManifest().getMainAttributes();
            String launcher = manifest.getValue("Main-Class");
            String application = manifest.getValue("Start-Class");
            assertEquals(GroceryApp.class.getName(), application);
            assertNotNull(launcher);
            assertNotNull(jar.getJarEntry(launcher.replace('.', '/') + ".class"));
            assertNotNull(jar.getJarEntry("BOOT-INF/classes/"
                + application.replace('.', '/') + ".class"));
        }
    }
}
