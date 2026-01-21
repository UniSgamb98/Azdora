package com.orodent.azdora.core.domain.repository;

import com.orodent.azdora.core.domain.model.ContactType;
import com.orodent.azdora.core.domain.model.GuestContact;

import java.util.List;

public interface GuestContactRepository {
    GuestContact insert(GuestContact contact);
    List<GuestContact> findByGuestId(long guestId);
    void updateType(long contactId, ContactType type);
    void updateValue(long contactId, String value);
    void deleteById(long contactId);
}
