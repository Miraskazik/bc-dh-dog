package cz.doghotel.dog.service;

import cz.doghotel.dog.domain.Dog;
import cz.doghotel.dog.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;

    @Transactional
    public Dog create(String name, UUID customerId, String breed, LocalDate birthDate, String notes) {
        return dogRepository.save(Dog.create(name, customerId, breed, birthDate, notes));
    }

    @Transactional(readOnly = true)
    public Dog get(UUID id) {
        return dogRepository.findById(id)
            .orElseThrow(() -> new DogNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<Dog> list(int page, int size, String search, UUID customerId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        String q = StringUtils.hasText(search) ? search.trim() : null;
        return dogRepository.search(q, customerId, pageable);
    }

    @Transactional
    public Dog update(UUID id, String name, UUID customerId, String breed, LocalDate birthDate, String notes) {
        Dog dog = get(id);
        dog.updateDetails(name, customerId, breed, birthDate, notes);
        return dog; // dirty checking uloží změnu při commitu
    }

    @Transactional
    public void delete(UUID id) {
        get(id).markDeleted(); // soft delete — get() hodí 404, pokud už neexistuje
    }
}
