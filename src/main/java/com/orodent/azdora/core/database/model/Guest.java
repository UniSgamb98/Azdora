package com.orodent.azdora.core.database.model;

public record Guest(
        Long id,
        String firstName,
        String lastName,
        String notes
) {}