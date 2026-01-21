package com.orodent.azdora.feature.reservation.service.impl;

import com.orodent.azdora.core.persistence.database.TransactionManager;
import com.orodent.azdora.core.domain.exception.ValidationException;
import com.orodent.azdora.core.domain.model.Guest;
import com.orodent.azdora.core.domain.model.Ota;
import com.orodent.azdora.core.domain.model.Reservation;
import com.orodent.azdora.core.domain.repository.GuestRepository;
import com.orodent.azdora.core.domain.repository.OtaRepository;
import com.orodent.azdora.core.domain.repository.ReservationRepository;
import com.orodent.azdora.feature.reservation.service.ReservationService;
import com.orodent.azdora.feature.reservation.service.dto.CreateReservationCommand;
import com.orodent.azdora.feature.reservation.service.dto.ReservationRowData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReservationServiceImpl implements ReservationService {

    private final GuestRepository guestRepo;
    private final ReservationRepository reservationRepo;
    private final OtaRepository otaRepo;
    private final TransactionManager tx;

    public ReservationServiceImpl(GuestRepository guestRepo, ReservationRepository reservationRepo, OtaRepository otaRepo, TransactionManager tx) {
        this.guestRepo = guestRepo;
        this.reservationRepo = reservationRepo;
        this.otaRepo = otaRepo;
        this.tx = tx;
    }

    @Override
    public List<Ota> loadOtas() {
        return otaRepo.findAll();
    }

    @Override
    public List<ReservationRowData> loadReservationRows() {
        List<ReservationRowData> out = new ArrayList<>();

        for (Reservation r : reservationRepo.findAll()) {
            Guest g = guestRepo.findById(r.guestId());
            Ota o = otaRepo.findById(r.otaId());

            out.add(new ReservationRowData(
                    r.id(),
                    g.firstName() + " " + g.lastName(),
                    o,
                    r.provenance(),
                    r.notes(),
                    r.createdAt(),
                    r.checkIn(),
                    r.checkOut(),
                    r.nights(),
                    r.adultGuestsCount(),
                    r.childGuestsCount(),
                    r.totalAmount()
            ));
        }
        return out;
    }

    @Override
    public void createReservation(CreateReservationCommand cmd) {
        // --- VALIDAZIONE ---
        if (cmd.guestText() == null || cmd.guestText().isBlank()) {
            throw new ValidationException("Cliente mancante");
        }
        if (cmd.otaId() <= 0) {
            throw new ValidationException("OTA non valida");
        }
        if (cmd.checkIn() == null || cmd.checkOut() == null || !cmd.checkOut().isAfter(cmd.checkIn())) {
            throw new ValidationException("Date non valide");
        }
        if (cmd.adultGuests() <= 0) {
            throw new ValidationException("Numero adulti non valido");
        }
        if (cmd.childGuests() < 0) {
            throw new ValidationException("Numero bambini non valido");
        }
        if (cmd.amount() == null || cmd.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Importo non valido");
        }

        long nights = ChronoUnit.DAYS.between(cmd.checkIn(), cmd.checkOut());

        // --- PARSE NOME/COGNOME ---
        String[] parts = cmd.guestText().trim().split("\\s+", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        tx.inTransaction(() -> {
            Guest guest = guestRepo.insert(new Guest(null, firstName, lastName, null));

            reservationRepo.insert(new Reservation(
                    null,
                    cmd.otaId(),
                    guest.id(),
                    cmd.provenance(),
                    cmd.checkIn(),
                    cmd.checkOut(),
                    nights,
                    cmd.adultGuests(),
                    cmd.childGuests(),
                    cmd.amount(),
                    cmd.createdAt(),
                    cmd.notes()
            ));
        });
    }

    @Override
    public void updateProvenance(long reservationId, String provenance) {
        if (provenance == null) provenance = "";
        reservationRepo.updateProvenance(reservationId, provenance);
    }

    @Override
    public void updateNotes(long reservationId, String notes) {
        if (notes == null) notes = "";
        reservationRepo.updateNotes(reservationId, notes);
    }

    @Override
    public void updateOta(long reservationId, Ota ota) {
        if (ota == null || ota.id() == null || ota.id() <= 0) {
            throw new ValidationException("OTA non valida");
        }
        reservationRepo.updateOta(reservationId, ota.id());
    }

    @Override
    public void updateDates(long reservationId, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new ValidationException("Check-out deve essere dopo check-in");
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        reservationRepo.updateDatesAndNights(reservationId, checkIn, checkOut, nights);
    }

    @Override
    public void updateAdultGuests(long reservationId, int adultGuests) {
        if (adultGuests <= 0) throw new ValidationException("Numero adulti non valido");
        reservationRepo.updateAdultGuestsCount(reservationId, adultGuests);
    }

    @Override
    public void updateChildGuests(long reservationId, int childGuests) {
        if (childGuests < 0) throw new ValidationException("Numero bambini non valido");
        reservationRepo.updateChildGuestsCount(reservationId, childGuests);
    }

    @Override
    public void updateAmount(long reservationId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Importo non valido");
        }
        reservationRepo.updateAmount(reservationId, amount);
    }
}
