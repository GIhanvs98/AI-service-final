package lk.gihanvs.knowledge.ai;

import lk.gihanvs.knowledge.ai.dto.request.IngestChunkRequest;
import lk.gihanvs.knowledge.ai.dto.request.QueryRequest;
import lk.gihanvs.knowledge.ai.dto.response.QueryResponse;
import lk.gihanvs.knowledge.ai.dto.response.SimilarChunkResponse;
import lk.gihanvs.knowledge.ai.service.AiService;
import lk.gihanvs.knowledge.ai.service.VectorStoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration and lifecycle tests for AiService and VectorStoreService.
 */
@SpringBootTest
class AiServiceApplicationTests {

    @Autowired
    private AiService aiService;

    @Autowired
    private VectorStoreService vectorStoreService;

    @Test
    void contextLoads() {
        assertNotNull(aiService, "AiService should be loaded in application context");
        assertNotNull(vectorStoreService, "VectorStoreService should be loaded in application context");
    }

    @Test
    void testIngestAndRAGQueryFlow() {
        UUID docId = UUID.randomUUID();
        UUID chunkId = UUID.randomUUID();

        IngestChunkRequest request = new IngestChunkRequest(
                docId,
                chunkId,
                0,
                "Microservices Architecture Guide",
                "Microservices architecture enables independent deployment, fault isolation, and decoupled data stores."
        );

        vectorStoreService.ingest(request);

        List<SimilarChunkResponse> searchResults = vectorStoreService.search("independent deployment architecture", 3, 0.1);
        assertFalse(searchResults.isEmpty(), "Search results should find matching chunks");
        assertEquals(docId, searchResults.getFirst().documentId());

        QueryResponse queryResponse = aiService.queryKnowledge(new QueryRequest("independent deployment architecture", 3, 0.1));
        assertNotNull(queryResponse.answer(), "Answer should not be null");
        assertFalse(queryResponse.citations().isEmpty(), "Citations should be present");
        assertTrue(queryResponse.answer().contains("Microservices"), "Answer should cite microservices content");

        vectorStoreService.deleteByDocumentId(docId);
    }
}
