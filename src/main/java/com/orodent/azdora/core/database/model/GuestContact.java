package com.orodent.azdora.core.database.model;

public record GuestContact(
        Long id,
        Long guestId,
        ContactType contactType,
        String contactValue
) {}