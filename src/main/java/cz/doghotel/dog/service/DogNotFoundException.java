package cz.doghotel.dog.service;

import java.util.UUID;

/** Pes neexistuje nebo je soft-deleted. Mapuje se na HTTP 404. */
public class DogNotFoundException extends RuntimeException {

    public DogNotFoundException(UUID id) {
        super("Pes nenalezen: " + id);
    }
}
