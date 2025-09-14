package br.com.mercadolibre.application.stock;

import br.com.mercadolibre.domain.event.EventStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockService {

    private final EventStockService eventRepository;

    public Integer getCurrentStockByProductId(String productId) {
        return eventRepository.findCurrentStockByProductId(productId);
    }
}
