package cz.doghotel.dog.mapper;

import cz.doghotel.dog.api.server.model.DogPage;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

/**
 * Mapuje doménovou entitu {@link cz.doghotel.dog.domain.Dog} na generované DTO
 * z kontraktu bc-dh-dog-api. Opačný směr (request → entita) řeší behaviorální
 * metody na entitě, aby entita zůstala zapouzdřená (bez setterů).
 */
@Mapper(componentModel = "spring")
public interface DogApiMapper {

    cz.doghotel.dog.api.server.model.Dog toDto(cz.doghotel.dog.domain.Dog entity);

    default DogPage toPage(Page<cz.doghotel.dog.domain.Dog> page) {
        DogPage dto = new DogPage();
        dto.setContent(page.getContent().stream().map(this::toDto).toList());
        dto.setPage(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        return dto;
    }
}
