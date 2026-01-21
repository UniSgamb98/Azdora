package com.orodent.azdora.core.domain.model;

public record GuestContact(
        Long id,
        Long guestId,
        ContactType contactType,
        String contactValue
) {}