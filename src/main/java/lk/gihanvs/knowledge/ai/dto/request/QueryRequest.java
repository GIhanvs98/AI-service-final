package lk.gihanvs.knowledge.ai.dto.request;

/**
 * Natural language search / question prompt payload.
 * Supports both 'query' and 'question' field names for flexible client consumption.
 */
public record QueryRequest(
        String query,
        String question,
        Integer topK,
        Double minScore
) {
    public QueryRequest(String query, Integer topK, Double minScore) {
        this(query, null, topK, minScore);
    }

    public String getEffectiveQuery() {
        if (query != null && !query.isBlank()) {
            return query.trim();
        }
        if (question != null && !question.isBlank()) {
            return question.trim();
        }
        return "";
    }
}
