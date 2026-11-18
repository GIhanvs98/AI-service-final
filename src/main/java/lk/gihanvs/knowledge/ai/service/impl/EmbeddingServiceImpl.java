package lk.gihanvs.knowledge.ai.service.impl;

import lk.gihanvs.knowledge.ai.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Implementation of EmbeddingService providing dense normalized vector projections
 * and cosine similarity calculations.
 */
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final int dimension;

    public EmbeddingServiceImpl(@Value("${app.ai.embedding-dimension:1536}") int dimension) {
        this.dimension = dimension;
    }

    @Override
    public float[] generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            return new float[dimension];
        }

        float[] vector = new float[dimension];
        String[] tokens = text.toLowerCase(Locale.ROOT).split("\\W+");

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            for (String token : tokens) {
                if (token.isBlank()) continue;

                byte[] hash1 = md5.digest(token.getBytes(StandardCharsets.UTF_8));
                byte[] hash2 = sha256.digest(token.getBytes(StandardCharsets.UTF_8));

                for (int i = 0; i < hash1.length; i++) {
                    int index = Math.abs((hash1[i] << 8) | (hash2[i] & 0xFF)) % dimension;
                    vector[index] += 1.0f + (hash2[i] % 5) * 0.1f;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            // Fallback deterministic projection
            for (int i = 0; i < text.length(); i++) {
                int index = Math.abs(text.charAt(i) * 31 + i) % dimension;
                vector[index] += 1.0f;
            }
        }

        // L2 Normalization
        float norm = 0.0f;
        for (float v : vector) {
            norm += v * v;
        }

        if (norm > 0) {
            float sqrtNorm = (float) Math.sqrt(norm);
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= sqrtNorm;
            }
        }

        return vector;
    }

    @Override
    public double calculateCosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA == null || vectorB == null || vectorA.length != vectorB.length) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        if (normA <= 0.0 || normB <= 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public String serializeVector(float[] vector) {
        StringBuilder sb = new StringBuilder(vector.length * 8);
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(vector[i]);
        }
        return sb.toString();
    }

    @Override
    public float[] deserializeVector(String vectorStr) {
        if (vectorStr == null || vectorStr.isBlank()) {
            return new float[dimension];
        }

        String[] parts = vectorStr.split(",");
        float[] vector = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Float.parseFloat(parts[i].trim());
        }
        return vector;
    }
}
