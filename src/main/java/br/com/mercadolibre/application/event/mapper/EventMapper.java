package br.com.mercadolibre.application.event.mapper;

import br.com.mercadolibre.api.model.CreateEventRequest;
import br.com.mercadolibre.infra.message.model.Payload;
import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "payload", expression = "java(toPayload(request))")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UpdateInventoryMessage toMessage(CreateEventRequest request);

    Payload toPayload(CreateEventRequest request);

}
