package com.orodent.azdora.feature.reservation.service.dto;

import com.orodent.azdora.core.database.model.ContactType;
import com.orodent.azdora.core.database.model.GuestContact;
import com.orodent.azdora.core.database.repository.GuestContactRepository;
import com.orodent.azdora.feature.contact.GuestContactService;

import java.util.List;

public class GuestContactServiceImpl implements GuestContactService {

    private final GuestContactRepository repo;

    public GuestContactServiceImpl(GuestContactRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<GuestContact> findByGuestId(long guestId) {
        return repo.findByGuestId(guestId);
    }

    @Override
    public GuestContact create(long guestId, ContactType type, String value) {
        if (guestId <= 0) throw new RuntimeException("guestId non valido");
        if (type == null) throw new RuntimeException("Tipo contatto mancante");
        if (value == null) value = "";
        return repo.insert(new GuestContact(null, guestId, type, value));
    }

    @Override
    public void updateType(long contactId, ContactType type) {
        if (type == null) throw new RuntimeException("Tipo contatto mancante");
        repo.updateType(contactId, type);
    }

    @Override
    public void updateValue(long contactId, String value) {
        if (value == null) value = "";
        repo.updateValue(contactId, value);
    }

    @Override
    public void delete(long contactId) {
        repo.deleteById(contactId);
    }
}
