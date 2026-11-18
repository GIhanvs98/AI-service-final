package lk.gihanvs.knowledge.ai.service.impl;

import lk.gihanvs.knowledge.ai.dto.request.QueryRequest;
import lk.gihanvs.knowledge.ai.dto.response.QueryResponse;
import lk.gihanvs.knowledge.ai.dto.response.SimilarChunkResponse;
import lk.gihanvs.knowledge.ai.service.AiService;
import lk.gihanvs.knowledge.ai.service.VectorStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of AiService coordinating context retrieval and RAG response synthesis.
 */
@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    private final VectorStoreService vectorStoreService;
    private final int defaultTopK;
    private final double defaultMinScore;

    public AiServiceImpl(
            VectorStoreService vectorStoreService,
            @Value("${app.ai.top-k:5}") int defaultTopK,
            @Value("${app.ai.similarity-threshold:0.7}") double defaultMinScore
    ) {
        this.vectorStoreService = vectorStoreService;
        this.defaultTopK = defaultTopK;
        this.defaultMinScore = defaultMinScore;
    }

    @Override
    public QueryResponse queryKnowledge(QueryRequest request) {
        long startTime = System.currentTimeMillis();

        int topK = (request.topK() != null && request.topK() > 0) ? request.topK() : defaultTopK;
        double minScore = (request.minScore() != null && request.minScore() >= 0.0) ? request.minScore() : defaultMinScore;

        log.info("Executing RAG query: '{}' (topK: {}, minScore: {})", request.query(), topK, minScore);

        List<SimilarChunkResponse> citations = vectorStoreService.search(request.query(), topK, minScore);

        String answer;
        if (citations.isEmpty()) {
            answer = "I could not find any matching documents or passages in the enterprise knowledge base to answer your question: '"
                    + request.query() + "'. Please try rephrasing or uploading relevant documentation.";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Based on the enterprise knowledge base, here is the answer to your query:\n\n");

            for (int i = 0; i < citations.size(); i++) {
                SimilarChunkResponse citation = citations.get(i);
                String title = citation.documentTitle() != null ? citation.documentTitle() : "Document";
                sb.append(String.format("[%d] (Source: %s, Chunk #%d, Relevance: %.1f%%)\n",
                        i + 1, title, citation.chunkIndex(), citation.similarityScore() * 100));
                sb.append(citation.content().trim()).append("\n\n");
            }
            answer = sb.toString().trim();
        }

        long processingTime = System.currentTimeMillis() - startTime;
        return new QueryResponse(request.query(), answer, citations, processingTime);
    }
}
