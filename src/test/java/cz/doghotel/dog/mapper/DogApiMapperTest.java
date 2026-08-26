package cz.doghotel.dog.mapper;

import cz.doghotel.dog.domain.Dog;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Testuje MapStruct impl vygenerovanou při kompilaci ({@code DogApiMapperImpl}). */
class DogApiMapperTest {

    private final DogApiMapper mapper = new DogApiMapperImpl();

    @Test
    void toDto_mapsAllFields() {
        UUID owner = UUID.randomUUID();
        Dog entity = Dog.create("Rex", owner, "Labrador", LocalDate.of(2020, 1, 1), "pozn");

        var dto = mapper.toDto(entity);

        assertThat(dto.getName()).isEqualTo("Rex");
        assertThat(dto.getCustomerId()).isEqualTo(owner);
        assertThat(dto.getBreed()).isEqualTo("Labrador");
        assertThat(dto.getBirthDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(dto.getNotes()).isEqualTo("pozn");
    }

    @Test
    void toPage_copiesContentAndPagingMetadata() {
        Page<Dog> page = new PageImpl<>(
            List.of(Dog.create("A", UUID.randomUUID(), null, null, null),
                    Dog.create("B", UUID.randomUUID(), null, null, null)),
            PageRequest.of(2, 5), 42);

        var dto = mapper.toPage(page);

        assertThat(dto.getContent()).hasSize(2);
        assertThat(dto.getPage()).isEqualTo(2);
        assertThat(dto.getSize()).isEqualTo(5);
        assertThat(dto.getTotalElements()).isEqualTo(42);
        assertThat(dto.getTotalPages()).isEqualTo(9);
    }
}
