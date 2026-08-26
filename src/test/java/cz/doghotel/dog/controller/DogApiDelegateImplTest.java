package cz.doghotel.dog.controller;

import cz.doghotel.dog.api.server.model.DogPage;
import cz.doghotel.dog.api.server.model.DogRequest;
import cz.doghotel.dog.domain.Dog;
import cz.doghotel.dog.mapper.DogApiMapper;
import cz.doghotel.dog.service.DogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogApiDelegateImplTest {

    @Mock
    DogService dogService;

    @Mock
    DogApiMapper mapper;

    @InjectMocks
    DogApiDelegateImpl delegate;

    @Test
    void createDog_returns201WithMappedBody() {
        UUID owner = UUID.randomUUID();
        DogRequest request = new DogRequest("Rex", owner);
        Dog entity = Dog.create("Rex", owner, null, null, null);
        var dto = new cz.doghotel.dog.api.server.model.Dog(
            UUID.randomUUID(), "Rex", owner, OffsetDateTime.now(), OffsetDateTime.now());
        when(dogService.create("Rex", owner, null, null, null)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        ResponseEntity<cz.doghotel.dog.api.server.model.Dog> response = delegate.createDog(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void deleteDog_returns204AndDelegatesToService() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = delegate.deleteDog(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(dogService).delete(id);
    }

    @Test
    void listDogs_appliesDefaultPagingWhenNull() {
        Page<Dog> page = new PageImpl<>(List.of());
        when(dogService.list(0, 20, null, null)).thenReturn(page);
        when(mapper.toPage(page)).thenReturn(new DogPage());

        delegate.listDogs(null, null, null, null);

        verify(dogService).list(0, 20, null, null);
    }
}
