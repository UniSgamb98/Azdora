package com.orodent.azdora.feature.reservation.controller;

import com.orodent.azdora.core.persistence.database.TransactionManager;
import com.orodent.azdora.core.domain.repository.GuestContactRepository;
import com.orodent.azdora.core.domain.repository.GuestRepository;
import com.orodent.azdora.core.domain.repository.OtaRepository;
import com.orodent.azdora.core.domain.repository.ReservationRepository;
import com.orodent.azdora.core.domain.model.Ota;
import com.orodent.azdora.feature.contact.service.GuestContactService;
import com.orodent.azdora.feature.contact.controller.GuestSearchController;
import com.orodent.azdora.feature.contact.service.GuestService;
import com.orodent.azdora.feature.contact.service.impl.GuestServiceImpl;
import com.orodent.azdora.feature.reservation.service.*;
import com.orodent.azdora.feature.contact.service.impl.GuestContactServiceImpl;
import com.orodent.azdora.feature.reservation.service.impl.ReservationServiceImpl;
import com.orodent.azdora.feature.reservation.view.model.ReservationRow;
import com.orodent.azdora.feature.reservation.service.dto.CreateReservationCommand;
import com.orodent.azdora.feature.reservation.service.dto.ReservationRowData;
import com.orodent.azdora.feature.reservation.view.ReservationMainView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservationMainController {

    private final ReservationMainView view;

    private final ReservationService service;
    private final ReservationTableBinder tableBinder;
    private final GuestSearchController guestSearchController;

    private final ObservableList<ReservationRow> rows = FXCollections.observableArrayList();
    private ReservationRow newRow;

    public ReservationMainController(
            ReservationMainView view,
            GuestRepository guestRepo,
            ReservationRepository reservationRepo,
            OtaRepository otaRepo,
            GuestContactRepository guestContactRepo,
            TransactionManager tx
    ) {
        this.view = view;
        this.service = new ReservationServiceImpl(guestRepo, reservationRepo, otaRepo, tx);
        this.tableBinder = new ReservationTableBinder(service, this::showError);
        GuestService guestService = new GuestServiceImpl(guestRepo);
        GuestContactService guestContactService = new GuestContactServiceImpl(guestContactRepo);
        this.guestSearchController = new GuestSearchController(view.getGuestSearchPane(), guestService, guestContactService);

        init();
    }

    private void init() {
        // Tabella
        tableBinder.bind(view.getTable());
        view.getTable().setItems(rows);

        // GuestSearchField
        guestSearchController.setOnGuestSelected(g -> {
            if (newRow != null) {
                newRow.setGuestName(g.firstName() + " " + g.lastName());
                view.getTable().refresh();
            }
        });

        view.getAddReservationButton().setOnAction(e -> saveNewReservation());
        view.getCancelReservationButton().setOnAction(e -> resetNewReservationRow());

        // Dati iniziali
        reloadTable();
    }

    private void reloadTable() {
        rows.setAll(
                service.loadReservationRows()
                        .stream()
                        .map(this::toRow)
                        .toList()
        );
        newRow = createEmptyRow();
        rows.add(newRow);
        attachNewRowListeners(newRow);
        updateNewReservationActions();
    }

    private ReservationRow toRow(ReservationRowData d) {
        return new ReservationRow(
                d.id(),
                d.guestFullName(),
                d.ota(),
                d.provenance(),
                d.notes(),
                d.createdAt(),
                d.checkIn(),
                d.checkOut(),
                d.nights(),
                d.adultGuests(),
                d.childGuests(),
                d.totalAmount()
        );
    }

    private ReservationRow createEmptyRow() {
        return new ReservationRow(
                0L,
                "",
                null,
                "",
                "",
                null,
                null,
                null,
                0L,
                0,
                0,
                null
        );
    }

    private void saveNewReservation() {
        try {
            if (newRow == null) {
                return;
            }

            String guestText = newRow.guestProperty().get();
            Ota ota = newRow.otaProperty().get();
            LocalDate checkIn = newRow.checkInProperty().get();
            LocalDate checkOut = newRow.checkOutProperty().get();
            LocalDate createdAt = newRow.prenotProperty().get();
            int adultGuests = newRow.adultGuestsProperty().get();
            int childGuests = newRow.childGuestsProperty().get();
            BigDecimal amount = newRow.amountProperty().get();
            String provenance = newRow.provenanceProperty().get();
            String notes = newRow.notesProperty().get();

            long otaId = (ota != null && ota.id() != null) ? ota.id() : 0L;
            CreateReservationCommand cmd = new CreateReservationCommand(
                    guestText,
                    otaId,
                    provenance,
                    createdAt,
                    checkIn,
                    checkOut,
                    adultGuests,
                    childGuests,
                    amount,
                    notes
            );

            service.createReservation(cmd);

            reloadTable();
        } catch (Exception ex) {
            showError("Errore durante il salvataggio: " + ex.getMessage());
        }
    }

    private void resetNewReservationRow() {
        if (newRow == null) {
            return;
        }
        newRow.setGuestName("");
        newRow.setOta(null);
        newRow.setProvenance("");
        newRow.setNotes("");
        newRow.setPrenotatoIl(null);
        newRow.setCheckIn(null);
        newRow.setCheckOut(null);
        newRow.setAdultGuestsCount(0);
        newRow.setChildGuestsCount(0);
        newRow.setAmount(null);
        view.getTable().refresh();
        updateNewReservationActions();
    }

    private void attachNewRowListeners(ReservationRow row) {
        row.guestProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.otaProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.provenanceProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.notesProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.prenotProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.checkInProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.checkOutProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.adultGuestsProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.childGuestsProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
        row.amountProperty().addListener((obs, oldValue, newValue) -> updateNewReservationActions());
    }

    private void updateNewReservationActions() {
        boolean hasData = hasNewReservationData();
        view.getNewReservationActions().setVisible(hasData);
        view.getNewReservationActions().setManaged(hasData);
    }

    private boolean hasNewReservationData() {
        if (newRow == null) {
            return false;
        }
        if (newRow.guestProperty().get() != null && !newRow.guestProperty().get().isBlank()) {
            return true;
        }
        if (newRow.otaProperty().get() != null) {
            return true;
        }
        if (newRow.provenanceProperty().get() != null && !newRow.provenanceProperty().get().isBlank()) {
            return true;
        }
        if (newRow.notesProperty().get() != null && !newRow.notesProperty().get().isBlank()) {
            return true;
        }
        if (newRow.prenotProperty().get() != null) {
            return true;
        }
        if (newRow.checkInProperty().get() != null || newRow.checkOutProperty().get() != null) {
            return true;
        }
        if (newRow.adultGuestsProperty().get() > 0 || newRow.childGuestsProperty().get() > 0) {
            return true;
        }
        return newRow.amountProperty().get() != null;
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
