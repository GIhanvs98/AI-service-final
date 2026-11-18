package lk.gihanvs.knowledge.ai.service.impl;

import lk.gihanvs.knowledge.ai.dto.request.IngestChunkRequest;
import lk.gihanvs.knowledge.ai.dto.response.SimilarChunkResponse;
import lk.gihanvs.knowledge.ai.entity.VectorEmbedding;
import lk.gihanvs.knowledge.ai.repository.VectorEmbeddingRepository;
import lk.gihanvs.knowledge.ai.service.EmbeddingService;
import lk.gihanvs.knowledge.ai.service.VectorStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of VectorStoreService managing persistent embeddings in PostgreSQL
 * and performing cosine similarity ranking.
 */
@Service
@Transactional
public class VectorStoreServiceImpl implements VectorStoreService {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreServiceImpl.class);

    private final VectorEmbeddingRepository repository;
    private final EmbeddingService embeddingService;
    private final int defaultDimension;

    public VectorStoreServiceImpl(
            VectorEmbeddingRepository repository,
            EmbeddingService embeddingService,
            @Value("${app.ai.embedding-dimension:1536}") int defaultDimension
    ) {
        this.repository = repository;
        this.embeddingService = embeddingService;
        this.defaultDimension = defaultDimension;
    }

    @Override
    public void ingest(IngestChunkRequest request) {
        float[] vector = embeddingService.generateEmbedding(request.content());
        String serialized = embeddingService.serializeVector(vector);

        VectorEmbedding embedding = repository.findByChunkId(request.chunkId())
                .orElse(new VectorEmbedding());

        embedding.setDocumentId(request.documentId());
        embedding.setChunkId(request.chunkId());
        embedding.setChunkIndex(request.chunkIndex());
        embedding.setDocumentTitle(request.documentTitle());
        embedding.setContent(request.content());
        embedding.setVectorData(serialized);
        embedding.setDimension(defaultDimension);

        repository.save(embedding);
        log.info("Ingested embedding for chunk {} of document {}", request.chunkId(), request.documentId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimilarChunkResponse> search(String queryText, int topK, double minScore) {
        float[] queryVector = embeddingService.generateEmbedding(queryText);
        List<VectorEmbedding> allEmbeddings = repository.findAll();

        return allEmbeddings.stream()
                .map(embedding -> {
                    float[] chunkVector = embeddingService.deserializeVector(embedding.getVectorData());
                    double score = embeddingService.calculateCosineSimilarity(queryVector, chunkVector);
                    return new SimilarChunkResponse(
                            embedding.getDocumentId(),
                            embedding.getChunkId(),
                            embedding.getChunkIndex(),
                            embedding.getDocumentTitle(),
                            embedding.getContent(),
                            Math.round(score * 1000.0) / 1000.0
                    );
                })
                .filter(res -> res.similarityScore() >= minScore)
                .sorted(Comparator.comparingDouble(SimilarChunkResponse::similarityScore).reversed())
                .limit(topK)
                .toList();
    }

    @Override
    public void deleteByDocumentId(UUID documentId) {
        repository.deleteAllByDocumentId(documentId);
        log.info("Deleted all vector embeddings for document {}", documentId);
    }
}
