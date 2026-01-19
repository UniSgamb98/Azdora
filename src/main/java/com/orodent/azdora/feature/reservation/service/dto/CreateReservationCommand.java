package com.orodent.azdora.feature.reservation.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateReservationCommand(
        String guestText,
        long otaId,
        String provenance,
        LocalDate createdAt,
        LocalDate checkIn,
        LocalDate checkOut,
        int adultGuests,
        int childGuests,
        BigDecimal amount,
        String notes
) {}
