package com.orodent.azdora.feature.contact.service;

import com.orodent.azdora.core.domain.model.ContactType;
import com.orodent.azdora.core.domain.model.GuestContact;

import java.util.List;

public interface GuestContactService {
    List<GuestContact> findByGuestId(long guestId);
    GuestContact create(long guestId, ContactType type, String value);
    void updateType(long contactId, ContactType type);
    void updateValue(long contactId, String value);
    void delete(long contactId);
}
