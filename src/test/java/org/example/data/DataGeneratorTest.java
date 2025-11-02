package org.example.data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class DataGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void testDataGeneration() throws Exception {
        File testFile = tempDir.resolve("test_output.json").toFile();

        assertDoesNotThrow(() -> {
            DataGenerator.genDAG(testFile.getAbsolutePath(), 5, 0.3, false);
        });

        assertTrue(testFile.exists());
        assertTrue(testFile.length() > 0);
    }

    @Test
    void testGraphProperties() throws Exception {
        File testFile = tempDir.resolve("test_graph.json").toFile();
        DataGenerator.genDAG(testFile.getAbsolutePath(), 10, 0.4, true);
        assertTrue(testFile.exists());
        assertTrue(testFile.length() > 0);
        File testFile2 = tempDir.resolve("test_graph2.json").toFile();
        DataGenerator.genDAG(testFile2.getAbsolutePath(), 15, 0.5, false);
        assertTrue(testFile2.exists());
    }

    @Test
    void testBackwardCompatibility() throws Exception {
        File testFile = tempDir.resolve("test_simple.json").toFile();

        assertDoesNotThrow(() -> {
            DataGenerator.genSimple(testFile.getAbsolutePath(), 8, 0.3, true, false);
        });

        assertTrue(testFile.exists());
    }

    @Test
    void testDifferentSizes() throws Exception {
        File smallFile = tempDir.resolve("small_test.json").toFile();
        File mediumFile = tempDir.resolve("medium_test.json").toFile();
        File largeFile = tempDir.resolve("large_test.json").toFile();

        DataGenerator.genDAG(smallFile.getAbsolutePath(), 6, 0.3, false);
        DataGenerator.genDAG(mediumFile.getAbsolutePath(), 15, 0.4, true);
        DataGenerator.genDAG(largeFile.getAbsolutePath(), 25, 0.5, false);

        assertTrue(smallFile.exists());
        assertTrue(mediumFile.exists());
        assertTrue(largeFile.exists());
    }
}
