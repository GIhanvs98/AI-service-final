package lk.gihanvs.knowledge.ai.controller;

import jakarta.validation.Valid;
import lk.gihanvs.knowledge.ai.dto.request.IngestChunkRequest;
import lk.gihanvs.knowledge.ai.dto.request.QueryRequest;
import lk.gihanvs.knowledge.ai.dto.response.ApiResponse;
import lk.gihanvs.knowledge.ai.dto.response.QueryResponse;
import lk.gihanvs.knowledge.ai.dto.response.SimilarChunkResponse;
import lk.gihanvs.knowledge.ai.service.AiService;
import lk.gihanvs.knowledge.ai.service.VectorStoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for semantic embeddings ingestion, cosine similarity search,
 * and contextual RAG queries.
 */
@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiService aiService;
    private final VectorStoreService vectorStoreService;

    public AiController(AiService aiService, VectorStoreService vectorStoreService) {
        this.aiService = aiService;
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<ApiResponse<Void>> ingestChunk(@Valid @RequestBody IngestChunkRequest request) {
        vectorStoreService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Chunk successfully embedded and stored", null));
    }

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<QueryResponse>> queryKnowledge(@Valid @RequestBody QueryRequest request) {
        QueryResponse response = aiService.queryKnowledge(request);
        return ResponseEntity.ok(ApiResponse.success("Query processed successfully", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SimilarChunkResponse>>> searchSimilar(
            @RequestParam("q") String query,
            @RequestParam(value = "topK", defaultValue = "5") int topK,
            @RequestParam(value = "minScore", defaultValue = "0.5") double minScore
    ) {
        List<SimilarChunkResponse> results = vectorStoreService.search(query, topK, minScore);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocumentVectors(@PathVariable UUID documentId) {
        vectorStoreService.deleteByDocumentId(documentId);
        return ResponseEntity.ok(ApiResponse.success("Document vectors deleted", null));
    }
}
