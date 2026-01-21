package com.orodent.azdora.core.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Reservation(
        Long id,
        Long otaId,
        Long guestId,
        String provenance,

        LocalDate checkIn,
        LocalDate checkOut,
        Long nights,

        int adultGuestsCount,
        int childGuestsCount,

        BigDecimal totalAmount,
        LocalDate createdAt,
        String notes
) {}
