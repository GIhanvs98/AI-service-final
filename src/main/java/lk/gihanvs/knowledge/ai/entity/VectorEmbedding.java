package lk.gihanvs.knowledge.ai.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing persistent vector embedding for a document chunk.
 */
@Entity
@Table(name = "vector_embeddings", indexes = {
        @Index(name = "idx_embeddings_document_id", columnList = "document_id"),
        @Index(name = "idx_embeddings_chunk_id", columnList = "chunk_id", unique = true)
})
public class VectorEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "chunk_id", nullable = false)
    private UUID chunkId;

    @Column(name = "chunk_index", nullable = false)
    private int chunkIndex;

    @Column(name = "document_title", length = 255)
    private String documentTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "vector_data", columnDefinition = "TEXT", nullable = false)
    private String vectorData;

    @Column(nullable = false)
    private int dimension;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public VectorEmbedding() {
    }

    public VectorEmbedding(UUID documentId, UUID chunkId, int chunkIndex, String documentTitle,
                           String content, String vectorData, int dimension) {
        this.documentId = documentId;
        this.chunkId = chunkId;
        this.chunkIndex = chunkIndex;
        this.documentTitle = documentTitle;
        this.content = content;
        this.vectorData = vectorData;
        this.dimension = dimension;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public UUID getChunkId() {
        return chunkId;
    }

    public void setChunkId(UUID chunkId) {
        this.chunkId = chunkId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVectorData() {
        return vectorData;
    }

    public void setVectorData(String vectorData) {
        this.vectorData = vectorData;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
