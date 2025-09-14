package br.com.mercadolibre.controller.event;

import br.com.mercadolibre.api.controller.EventsApi;
import br.com.mercadolibre.api.model.CreateEventRequest;
import br.com.mercadolibre.api.model.EventResponse;
import br.com.mercadolibre.application.event.EventApiService;
import br.com.mercadolibre.application.event.StockMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController implements EventsApi {

    private final EventApiService eventApiService;
    private final StockMigrationService migrationService;

    @Override
    public ResponseEntity<EventResponse> createEvent(CreateEventRequest request) {


        eventApiService.createEvent(request);

        var response = EventResponse.builder()
                .eventId(request.getEventId())
                .source("Endpoint: " + request.getSource())
                .productId(request.getProductId())
                .storeId(request.getStoreId())
                .quantity(request.getQuantity())
                .availableQuantity(request.getAvailableQuantity())
                .reservedQuantity(request.getReservedQuantity())
                .eventType(request.getEventType())
                .build();

        return new ResponseEntity<>(response, CREATED);
    }

    @Override
    public ResponseEntity<Void> migrateInventory() {
        migrationService.migrateStockToEvents();
        return ResponseEntity.noContent().build();
    }
}
