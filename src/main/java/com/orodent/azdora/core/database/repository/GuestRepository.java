package com.orodent.azdora.core.database.repository;

import com.orodent.azdora.core.database.model.Guest;

public interface GuestRepository {
    Guest insert(Guest guest);
    Guest findById(long id);
}
