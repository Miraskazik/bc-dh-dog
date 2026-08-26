package cz.doghotel.dog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Pes a jeho profil. Odkazuje na majitele přes {@code customerId} (id zákazníka
 * z bc-dh-customer) — žádnou kopii dat majitele nedrží. Soft delete přes
 * {@code deletedAt} — smazané záznamy se z běžných dotazů filtrují díky {@link SQLRestriction}.
 */
@Entity
@Table(name = "dogs")
@SQLRestriction("deleted_at is null")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(length = 255)
    private String breed;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private OffsetDateTime deletedAt;

    public static Dog create(String name, UUID customerId, String breed, LocalDate birthDate, String notes) {
        Dog d = new Dog();
        d.name = name;
        d.customerId = customerId;
        d.breed = breed;
        d.birthDate = birthDate;
        d.notes = notes;
        return d;
    }

    public void updateDetails(String name, UUID customerId, String breed, LocalDate birthDate, String notes) {
        this.name = name;
        this.customerId = customerId;
        this.breed = breed;
        this.birthDate = birthDate;
        this.notes = notes;
    }

    /** Soft delete — jen nastaví příznak, řádek v DB zůstává. */
    public void markDeleted() {
        this.deletedAt = OffsetDateTime.now();
    }
}
