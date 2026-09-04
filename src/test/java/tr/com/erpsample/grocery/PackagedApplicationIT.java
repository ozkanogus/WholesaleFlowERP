package tr.com.erpsample.grocery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.jar.Attributes;
import java.util.jar.JarFile;
import org.junit.jupiter.api.Test;

/** Runs in Maven verify after Spring Boot has repackaged the artifact. */
class PackagedApplicationIT {
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
