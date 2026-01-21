package com.orodent.azdora.feature.reservation.service.dto;

import com.orodent.azdora.core.database.model.Ota;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationRowData(
        long id,
        String guestFullName,
        Ota ota,
        String provenance,
        LocalDate createdAt,
        LocalDate checkIn,
        LocalDate checkOut,
        long nights,
        int adultGuests,
        int childGuests,
        BigDecimal totalAmount
) {}
