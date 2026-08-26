package cz.doghotel.dog.repository;

import cz.doghotel.dog.domain.Dog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Smazané záznamy (deletedAt != null) jsou globálně odfiltrované přes
 * {@code @SQLRestriction} na entitě, takže {@link #search} vrací jen aktivní psy.
 * Oba filtry jsou volitelné: {@code q} (jméno/plemeno) i {@code customerId} (majitel).
 */
public interface DogRepository extends JpaRepository<Dog, UUID> {

    @Query("""
        select d from Dog d
        where (:q is null
               or lower(d.name) like lower(concat('%', :q, '%'))
               or lower(d.breed) like lower(concat('%', :q, '%')))
          and (:customerId is null or d.customerId = :customerId)
        """)
    Page<Dog> search(@Param("q") String q, @Param("customerId") UUID customerId, Pageable pageable);
}
