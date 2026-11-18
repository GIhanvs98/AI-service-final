package lk.gihanvs.knowledge.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Natural language search / question prompt payload.
 */
public record QueryRequest(
        @NotBlank(message = "Query text is required")
        String query,

        Integer topK,

        Double minScore
) {
}
