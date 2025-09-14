package br.com.mercadolibre.infra.sql.event.repository;

import br.com.mercadolibre.infra.sql.event.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, String> {

    @Query("SELECT e FROM EventEntity e WHERE e.processed = false ORDER BY e.createdAt ASC")
    List<EventEntity> findUnprocessedEvents();

    @Query("SELECT e FROM EventEntity e WHERE e.aggregateId = :aggregateId ORDER BY e.createdAt DESC")
    List<EventEntity> findByAggregateIdOrderByCreatedAtDesc(@Param("aggregateId") String aggregateId);

    @Query("SELECT e FROM EventEntity e WHERE e.productId = :productId AND e.storeId = :storeId ORDER BY e.createdAt DESC")
    List<EventEntity> findByProductIdAndStoreIdOrderByCreatedAtDesc(
            @Param("productId") String productId, 
            @Param("storeId") String storeId
    );

    @Query("SELECT e FROM EventEntity e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    List<EventEntity> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate
    );

    Optional<EventEntity> findByEventId(String eventId);
}
