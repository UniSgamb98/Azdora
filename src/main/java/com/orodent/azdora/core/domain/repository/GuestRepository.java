package com.orodent.azdora.core.domain.repository;

import com.orodent.azdora.core.domain.model.Guest;

import java.util.List;

public interface GuestRepository {
    Guest insert(Guest guest);
    Guest findById(long id);
    List<Guest> findAll();
    void updateName(long id, String firstName, String lastName);
    void updateNotes(long id, String notes);
    void deleteById(long id);
}
