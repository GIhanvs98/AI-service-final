package lk.gihanvs.knowledge.ai.service;

import lk.gihanvs.knowledge.ai.dto.request.QueryRequest;
import lk.gihanvs.knowledge.ai.dto.response.QueryResponse;

/**
 * Service contract for RAG retrieval and conversational answer synthesis.
 */
public interface AiService {

    QueryResponse queryKnowledge(QueryRequest request);
}
