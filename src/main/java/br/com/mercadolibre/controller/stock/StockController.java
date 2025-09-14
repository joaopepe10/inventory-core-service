package br.com.mercadolibre.controller.stock;


import br.com.mercadolibre.api.controller.StockApi;
import br.com.mercadolibre.api.model.GetCurrentStockByProduct200Response;
import br.com.mercadolibre.application.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StockController implements StockApi {

    private final StockService stockService;

    @Override
    public ResponseEntity<GetCurrentStockByProduct200Response> getCurrentStockByProduct(String productId) {
        var currentStock = stockService.getCurrentStockByProductId(productId);

        var response = GetCurrentStockByProduct200Response.builder()
                .quantity(currentStock)
                .build();

        return ResponseEntity.ok(response);
    }
}
