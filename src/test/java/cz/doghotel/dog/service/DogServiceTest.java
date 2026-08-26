package cz.doghotel.dog.service;

import cz.doghotel.dog.domain.Dog;
import cz.doghotel.dog.repository.DogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    DogRepository dogRepository;

    @InjectMocks
    DogService dogService;

    @Test
    void create_savesNewDogWithGivenData() {
        UUID owner = UUID.randomUUID();
        when(dogRepository.save(any(Dog.class))).thenAnswer(inv -> inv.getArgument(0));

        Dog result = dogService.create("Rex", owner, "Labrador", LocalDate.of(2020, 1, 1), "pozn");

        ArgumentCaptor<Dog> saved = ArgumentCaptor.forClass(Dog.class);
        verify(dogRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Rex");
        assertThat(saved.getValue().getCustomerId()).isEqualTo(owner);
        assertThat(saved.getValue().getBreed()).isEqualTo("Labrador");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(result.getNotes()).isEqualTo("pozn");
    }

    @Test
    void get_returnsDog_whenExists() {
        UUID id = UUID.randomUUID();
        Dog dog = Dog.create("Bella", UUID.randomUUID(), null, null, null);
        when(dogRepository.findById(id)).thenReturn(Optional.of(dog));

        assertThat(dogService.get(id)).isSameAs(dog);
    }

    @Test
    void get_throwsNotFound_whenMissingOrSoftDeleted() {
        UUID id = UUID.randomUUID();
        when(dogRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dogService.get(id))
            .isInstanceOf(DogNotFoundException.class);
    }

    @Test
    void list_withoutSearch_passesNullQuery() {
        Page<Dog> page = new PageImpl<>(List.of(Dog.create("A", UUID.randomUUID(), null, null, null)));
        when(dogRepository.search(isNull(), isNull(), any(Pageable.class))).thenReturn(page);

        Page<Dog> result = dogService.list(0, 20, "   ", null);

        assertThat(result).isSameAs(page);
        verify(dogRepository).search(isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void list_withSearch_trimsAndDelegates() {
        Page<Dog> page = new PageImpl<>(List.of());
        when(dogRepository.search(anyString(), isNull(), any(Pageable.class))).thenReturn(page);

        dogService.list(1, 10, "  reh  ", null);

        ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);
        verify(dogRepository).search(query.capture(), isNull(), any(Pageable.class));
        assertThat(query.getValue()).isEqualTo("reh");
    }

    @Test
    void list_withCustomerId_filtersByOwner() {
        UUID owner = UUID.randomUUID();
        Page<Dog> page = new PageImpl<>(List.of());
        when(dogRepository.search(isNull(), eq(owner), any(Pageable.class))).thenReturn(page);

        dogService.list(0, 20, null, owner);

        verify(dogRepository).search(isNull(), eq(owner), any(Pageable.class));
    }

    @Test
    void update_modifiesExistingDog() {
        UUID id = UUID.randomUUID();
        UUID newOwner = UUID.randomUUID();
        Dog dog = Dog.create("Old", UUID.randomUUID(), "mix", null, "n");
        when(dogRepository.findById(id)).thenReturn(Optional.of(dog));

        Dog result = dogService.update(id, "New", newOwner, "Pudl", LocalDate.of(2021, 2, 2), "nn");

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getCustomerId()).isEqualTo(newOwner);
        assertThat(result.getBreed()).isEqualTo("Pudl");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2021, 2, 2));
        assertThat(result.getNotes()).isEqualTo("nn");
    }

    @Test
    void delete_setsDeletedFlagAndDoesNotHardDelete() {
        UUID id = UUID.randomUUID();
        Dog dog = Dog.create("Del", UUID.randomUUID(), null, null, null);
        when(dogRepository.findById(id)).thenReturn(Optional.of(dog));

        dogService.delete(id);

        assertThat(dog.getDeletedAt()).isNotNull();
        verify(dogRepository, org.mockito.Mockito.never()).delete(any(Dog.class));
    }
}
