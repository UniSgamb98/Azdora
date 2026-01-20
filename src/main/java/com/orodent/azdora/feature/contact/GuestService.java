package com.orodent.azdora.feature.contact;

import com.orodent.azdora.core.database.model.Guest;

import java.util.List;
import java.util.Optional;

public interface GuestService {

    List<Guest> findAll();

    /**
     * Ricerca "intelligente" con ranking e supporto a "nome cognome" o "cognome nome".
     * Ritorna i migliori match in ordine.
     */
    List<Guest> search(String query, int limit);

    /**
     * Primo match (se ti serve per autocomplete).
     */
    Optional<Guest> bestMatch(String query);

    void updateNotes(long guestId, String notes);

    // Future-proof: operazioni CRUD
    Guest createGuest(String firstName, String lastName);
    Guest updateGuest(long id, String firstName, String lastName);
    void deleteGuest(long id);
}
