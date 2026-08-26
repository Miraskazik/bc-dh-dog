package cz.doghotel.dog.controller;

import cz.doghotel.dog.api.server.DogsApiDelegate;
import cz.doghotel.dog.api.server.model.Dog;
import cz.doghotel.dog.api.server.model.DogPage;
import cz.doghotel.dog.api.server.model.DogRequest;
import cz.doghotel.dog.mapper.DogApiMapper;
import cz.doghotel.dog.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementace delegate rozhraní vygenerovaného z openapi.yaml. Generovaný
 * {@code DogsApiController} (z artefaktu bc-dh-dog-api) na tuto službu deleguje.
 * Vrstva pouze překládá HTTP ↔ doménu; veškerá logika je v {@link DogService}.
 */
@Service
@RequiredArgsConstructor
public class DogApiDelegateImpl implements DogsApiDelegate {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    private final DogService dogService;
    private final DogApiMapper mapper;

    @Override
    public ResponseEntity<Dog> createDog(DogRequest request) {
        var created = dogService.create(
            request.getName(), request.getCustomerId(), request.getBreed(),
            request.getBirthDate(), request.getNotes());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    @Override
    public ResponseEntity<Dog> getDog(UUID id) {
        return ResponseEntity.ok(mapper.toDto(dogService.get(id)));
    }

    @Override
    public ResponseEntity<DogPage> listDogs(Integer page, Integer size, String search, UUID customerId) {
        var result = dogService.list(
            page != null ? page : DEFAULT_PAGE,
            size != null ? size : DEFAULT_SIZE,
            search,
            customerId);
        return ResponseEntity.ok(mapper.toPage(result));
    }

    @Override
    public ResponseEntity<Dog> updateDog(UUID id, DogRequest request) {
        var updated = dogService.update(
            id, request.getName(), request.getCustomerId(), request.getBreed(),
            request.getBirthDate(), request.getNotes());
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @Override
    public ResponseEntity<Void> deleteDog(UUID id) {
        dogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
