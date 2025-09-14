package br.com.mercadolibre.domain.event.mapper;

import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import br.com.mercadolibre.infra.sql.event.model.EventStockEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventStockMapper {

    @Mapping(target = "availableQuantity", source = "message.payload.availableQuantity")
    @Mapping(target = "reservedQuantity", source = "message.payload.reservedQuantity")
    @Mapping(target = "quantity", source = "message.payload.quantity")
    @Mapping(target = "productId", source = "message.payload.productId")
    @Mapping(target = "storeId", source = "message.payload.storeId")
    EventStockEntity toEntity(UpdateInventoryMessage message);
}
