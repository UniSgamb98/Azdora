package com.orodent.azdora.feature.reservation.controller;

import com.orodent.azdora.core.persistence.database.TransactionManager;
import com.orodent.azdora.core.domain.model.Ota;
import com.orodent.azdora.core.domain.repository.GuestContactRepository;
import com.orodent.azdora.core.domain.repository.GuestRepository;
import com.orodent.azdora.core.domain.repository.OtaRepository;
import com.orodent.azdora.core.domain.repository.ReservationRepository;
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
import java.util.List;

public class ReservationMainController {

    private final ReservationMainView view;

    private final ReservationService service;
    private final ReservationTableBinder tableBinder;
    private final GuestSearchController guestSearchController;

    private final ObservableList<ReservationRow> rows = FXCollections.observableArrayList();

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
        guestSearchController.setOnGuestSelected(g -> view.getFormPane().getGuestField().setText(g.firstName() + " " + g.lastName()));

        // Combo OTA
        List<Ota> otas = service.loadOtas();
        view.getFormPane().getOtaBox().setItems(FXCollections.observableArrayList(otas));

        // Bottone salva
        view.getFormPane().getSaveButton().setOnAction(e -> saveReservation());

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
    }

    private ReservationRow toRow(ReservationRowData d) {
        return new ReservationRow(
                d.id(),
                d.guestFullName(),
                d.ota(),
                d.provenance(),
                d.createdAt(),
                d.checkIn(),
                d.checkOut(),
                d.nights(),
                d.adultGuests(),
                d.childGuests(),
                d.totalAmount()
        );
    }

    private void saveReservation() {
        var form = view.getFormPane();

        try {
            String guestText = form.getGuestField().getText();
            Ota ota = form.getOtaBox().getValue();
            LocalDate checkIn = form.getCheckInPicker().getValue();
            LocalDate checkOut = form.getCheckOutPicker().getValue();
            LocalDate createdAt = form.getCreatedAtPicker().getValue();

            String adultGuestsText = form.getAdultGuestsCountField().getText();
            String childGuestsText = form.getChildGuestsCountField().getText();
            String provenance = form.getProvenanceField().getText();
            String notes = form.getNotesArea().getText();
            String amountText = form.getAmount().getText();

            int adultGuests = parseIntStrict(adultGuestsText, "Numero adulti non valido", true);
            int childGuests = parseIntStrict(childGuestsText, "Numero bambini non valido", false);
            BigDecimal amount = parseBigDecimal(amountText);

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
            view.getFormPane().clear();


        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Errore durante il salvataggio: " + ex.getMessage());
        }
    }

    private int parseIntStrict(String text, String errorMessage, boolean mustBePositive) {
        if (text == null) throw new IllegalArgumentException(errorMessage);
        try {
            int v = Integer.parseInt(text.trim());
            if (mustBePositive && v <= 0) throw new IllegalArgumentException(errorMessage);
            if (!mustBePositive && v < 0) throw new IllegalArgumentException(errorMessage);
            return v;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private BigDecimal parseBigDecimal(String text) {
        if (text == null) throw new IllegalArgumentException("Importo non valido");
        try {
            BigDecimal v = new BigDecimal(text.trim());
            if (v.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Importo non valido");
            return v;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Importo non valido");
        }
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
