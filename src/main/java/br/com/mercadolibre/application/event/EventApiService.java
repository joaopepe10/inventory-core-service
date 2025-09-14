package br.com.mercadolibre.application.event;

import br.com.mercadolibre.api.model.CreateEventRequest;
import br.com.mercadolibre.application.event.mapper.EventMapper;
import br.com.mercadolibre.domain.event.EventStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventApiService {

    private final EventStockService eventStockService;
    private final EventMapper eventMapper;

    public void createEvent(CreateEventRequest request) {
        var payload = eventMapper.toMessage(request);
        eventStockService.processEvent(payload);
    }
}
