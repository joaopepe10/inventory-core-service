package br.com.mercadolibre.application.stock;

import br.com.mercadolibre.domain.event.EventStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockService {

    private final EventStockService eventStockService;

    public Integer getCurrentStockByProductId(String productId) {
        return eventStockService.findCurrentStockByProductId(productId);
    }

    public Integer getCurrentStockByProductIdAndStoreId(String productId, String storeId) {
        return eventStockService.findCurrentStockByProductIdAndStoreId(productId, storeId);
    }
}
