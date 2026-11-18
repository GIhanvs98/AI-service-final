package lk.gihanvs.knowledge.ai.dto.response;

import java.util.UUID;

/**
 * Representation of a matched semantic document chunk with its similarity score.
 */
public record SimilarChunkResponse(
        UUID documentId,
        UUID chunkId,
        int chunkIndex,
        String documentTitle,
        String content,
        double similarityScore
) {
}
