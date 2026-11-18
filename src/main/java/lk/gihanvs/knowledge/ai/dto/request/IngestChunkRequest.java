package lk.gihanvs.knowledge.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Payload to ingest a document chunk into the vector embedding space.
 */
public record IngestChunkRequest(
        @NotNull(message = "Document ID is required")
        UUID documentId,

        @NotNull(message = "Chunk ID is required")
        UUID chunkId,

        int chunkIndex,

        String documentTitle,

        @NotBlank(message = "Content cannot be blank")
        String content
) {
}
