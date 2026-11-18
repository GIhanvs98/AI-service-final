package lk.gihanvs.knowledge.ai.repository;

import lk.gihanvs.knowledge.ai.entity.VectorEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data access repository for VectorEmbedding entities.
 */
@Repository
public interface VectorEmbeddingRepository extends JpaRepository<VectorEmbedding, UUID> {

    List<VectorEmbedding> findAllByDocumentId(UUID documentId);

    Optional<VectorEmbedding> findByChunkId(UUID chunkId);

    void deleteAllByDocumentId(UUID documentId);

    long countByDocumentId(UUID documentId);
}
