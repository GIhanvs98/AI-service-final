package lk.gihanvs.knowledge.ai.service;

/**
 * Service contract for text vectorization and cosine similarity calculations.
 */
public interface EmbeddingService {

    float[] generateEmbedding(String text);

    double calculateCosineSimilarity(float[] vectorA, float[] vectorB);

    String serializeVector(float[] vector);

    float[] deserializeVector(String vectorStr);
}
