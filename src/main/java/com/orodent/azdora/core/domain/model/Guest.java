package com.orodent.azdora.core.domain.model;

public record Guest(
        Long id,
        String firstName,
        String lastName,
        String notes
) {}