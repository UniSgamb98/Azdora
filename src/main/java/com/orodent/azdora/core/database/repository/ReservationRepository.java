package com.orodent.azdora.core.database.repository;

import com.orodent.azdora.core.database.model.Reservation;

import java.util.List;

public interface ReservationRepository {
    void insert(Reservation reservation);
    List<Reservation> findAll();
    void updateProvenance(long reservationId, String provenance);
}
