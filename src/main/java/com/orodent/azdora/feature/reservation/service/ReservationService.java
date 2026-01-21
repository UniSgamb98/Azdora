package com.orodent.azdora.feature.reservation.service;

import com.orodent.azdora.core.domain.model.Ota;
import com.orodent.azdora.feature.reservation.service.dto.CreateReservationCommand;
import com.orodent.azdora.feature.reservation.service.dto.ReservationRowData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    // dati per la UI
    List<Ota> loadOtas();

    List<ReservationRowData> loadReservationRows();

    // create
    void createReservation(CreateReservationCommand cmd);

    // update (da tabella editabile)
    void updateProvenance(long reservationId, String provenance);

    void updateNotes(long reservationId, String notes);

    void updateOta(long reservationId, Ota ota);

    void updateDates(long reservationId, LocalDate checkIn, LocalDate checkOut);

    void updateAdultGuests(long reservationId, int adultGuests);

    void updateChildGuests(long reservationId, int childGuests);

    void updateAmount(long reservationId, BigDecimal amount);
}
