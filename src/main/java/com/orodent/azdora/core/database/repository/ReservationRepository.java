package com.orodent.azdora.core.database.repository;

import com.orodent.azdora.core.database.model.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository {
    void insert(Reservation reservation);
    List<Reservation> findAll();
    void updateProvenance(long reservationId, String provenance);
    void updateAdultGuestsCount(long id, int newValue);
    void updateChildGuestsCount(long id, int newValue);
    void updateAmount(long id, BigDecimal value);
    void updateDatesAndNights(long reservationId, LocalDate checkIn, LocalDate checkOut, long nights);
}
