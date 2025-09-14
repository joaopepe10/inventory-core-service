package br.com.mercadolibre.infra.sql.event.repository;

import br.com.mercadolibre.infra.sql.event.model.EventStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventStockEntity, String> {

    Optional<EventStockEntity> findByEventId(String eventId);

    List<EventStockEntity> findCurrentStockByProductId(String productId);
}
