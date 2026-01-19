package com.orodent.azdora.feature.reservation.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationRowData(
        long id,
        String guestFullName,
        String otaName,
        String provenance,
        LocalDate createdAt,
        LocalDate checkIn,
        LocalDate checkOut,
        long nights,
        int adultGuests,
        int childGuests,
        BigDecimal totalAmount
) {}
