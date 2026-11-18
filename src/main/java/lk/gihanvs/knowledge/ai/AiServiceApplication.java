package lk.gihanvs.knowledge.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * AI and RAG Service for the AI-Powered Enterprise Knowledge Assistant.
 * Provides semantic vector embeddings, cosine similarity search, context augmentation,
 * and intelligent question-answering with citation tracking.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AiServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AiServiceApplication.class);

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(AiServiceApplication.class, args);
    }

    /**
     * Loads key-value pairs from a local .env file into System properties if not already set.
     */
    private static void loadDotEnv() {
        Path[] candidatePaths = new Path[]{
                Path.of(".env"),
                Path.of("../.env")
        };

        for (Path path : candidatePaths) {
            if (Files.isRegularFile(path)) {
                log.info("Loading environment configuration from: {}", path.toAbsolutePath());
                try (Stream<String> lines = Files.lines(path)) {
                    lines.map(String::trim)
                            .filter(line -> !line.isEmpty() && !line.startsWith("#") && line.contains("="))
                            .forEach(line -> {
                                int delimiterIndex = line.indexOf('=');
                                String key = line.substring(0, delimiterIndex).trim();
                                String value = line.substring(delimiterIndex + 1).trim();

                                if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                        (value.startsWith("'") && value.endsWith("'"))) {
                                    value = value.substring(1, value.length() - 1);
                                }

                                if (System.getProperty(key) == null && System.getenv(key) == null) {
                                    System.setProperty(key, value);
                                }
                            });
                    break;
                } catch (IOException e) {
                    log.warn("Failed to read environment file at {}: {}", path, e.getMessage());
                }
            }
        }
    }
}
