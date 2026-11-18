package lk.gihanvs.knowledge.ai.service;

import lk.gihanvs.knowledge.ai.dto.request.IngestChunkRequest;
import lk.gihanvs.knowledge.ai.dto.response.SimilarChunkResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for persistent vector storage and semantic nearest-neighbor search.
 */
public interface VectorStoreService {

    void ingest(IngestChunkRequest request);

    List<SimilarChunkResponse> search(String queryText, int topK, double minScore);

    void deleteByDocumentId(UUID documentId);
}
