package br.com.mercadolibre.infra.sql.event.model;

import br.com.mercadolibre.infra.message.model.ChangeType;
import br.com.mercadolibre.infra.message.model.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

    @Id
    @Column(name = "event_id", length = 50, nullable = false, updatable = false)
    private String eventId;

    @Enumerated(STRING)
    @Column(name = "event_type", length = 20, nullable = false)
    private EventType eventType;

    @Enumerated(STRING)
    @Column(name = "change_type", length = 20, nullable = false)
    private ChangeType changeType;

    @Column(name = "aggregate_id", length = 50, nullable = false)
    private String aggregateId;

    @Column(name = "source", length = 50, nullable = false)
    private String source;

    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;

    @Column(name = "store_id", nullable = false, length = 50)
    private String storeId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "available_quantity")
    private Integer availableQuantity;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;

    @Column(name = "processed", nullable = false)
    @Builder.Default
    private Boolean processed = false;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}

