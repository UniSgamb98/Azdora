package com.orodent.azdora.feature.reservation.controller;

import com.orodent.azdora.app.FileService;
import com.orodent.azdora.core.database.model.Guest;
import com.orodent.azdora.core.database.model.Ota;
import com.orodent.azdora.core.database.model.Reservation;
import com.orodent.azdora.core.database.repository.GuestRepository;
import com.orodent.azdora.core.database.repository.OtaRepository;
import com.orodent.azdora.core.database.repository.ReservationRepository;
import com.orodent.azdora.feature.reservation.model.ReservationRow;
import com.orodent.azdora.feature.reservation.view.ReservationMainView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class ReservationMainController {
    private final ReservationMainView view;
    private final GuestRepository guestRepo;
    private final ReservationRepository reservationRepo;
    private final OtaRepository otaRepo;
    private final ObservableList<ReservationRow> rows = FXCollections.observableArrayList();


    public ReservationMainController(ReservationMainView view, GuestRepository guestRepo, ReservationRepository reservationRepo, OtaRepository otaRepo) {
        this.view = view;
        this.guestRepo = guestRepo;
        this.reservationRepo = reservationRepo;
        this.otaRepo = otaRepo;

        initTable();
        initForm();
        loadReservations();

        view.getFormPane().getSaveButton().setOnAction( e -> saveReservation());
    }

    private void initForm() {
        List<Ota> otaList = otaRepo.findAll();
        if (otaList.isEmpty()) {
            otaList = FileService.loadProducts();
        }
        view.getFormPane().getOtaBox().getItems().addAll(otaList);
    }

    private void initTable() {
        var table = view.getTable();
        table.setEditable(true);

        TableColumn<ReservationRow, String> guestCol =
                new TableColumn<>("Cliente");
        guestCol.setCellValueFactory(d -> d.getValue().guestProperty());

        TableColumn<ReservationRow, String> otaCol =
                new TableColumn<>("OTA");
        otaCol.setCellValueFactory(d -> d.getValue().otaProperty());

        TableColumn<ReservationRow, LocalDate> prenotCol =
                new TableColumn<>("Prenotato il");
        prenotCol.setCellValueFactory(d -> d.getValue().prenotProperty());

        TableColumn<ReservationRow, String> provenanceCol =
                new TableColumn<>("Provenienza");
        provenanceCol.setCellValueFactory(d -> d.getValue().provenanceProperty());
        provenanceCol.setCellFactory(TextFieldTableCell.forTableColumn());
        provenanceCol.setOnEditCommit(event -> {
            ReservationRow row = event.getRowValue();
            String newValue = event.getNewValue();

            row.setProvenance(newValue);

            reservationRepo.updateProvenance(row.getId(), newValue);
        });


        TableColumn<ReservationRow, Long> nightsCol =
                new TableColumn<>("notti");
        nightsCol.setCellValueFactory(d -> d.getValue().nightsCountProperty().asObject());

        TableColumn<ReservationRow, LocalDate> inCol =
                new TableColumn<>("Check-in");
        inCol.setCellValueFactory(d -> d.getValue().checkInProperty());

        TableColumn<ReservationRow, LocalDate> outCol =
                new TableColumn<>("Check-out");
        outCol.setCellValueFactory(d -> d.getValue().checkOutProperty());

        TableColumn<ReservationRow, Integer> adultGuestsCol =
                new TableColumn<>("Adulti");
        adultGuestsCol.setCellValueFactory(d -> d.getValue().adultGuestsProperty().asObject());

        TableColumn<ReservationRow, Integer> childGuestsCol =
                new TableColumn<>("Bambini");
        childGuestsCol.setCellValueFactory(d -> d.getValue().childGuestsProperty().asObject());

        TableColumn<ReservationRow, BigDecimal> amountCol =
                new TableColumn<>("Fatturato");
        amountCol.setCellValueFactory(d -> d.getValue().amountProperty());
        NumberFormat euro = NumberFormat.getCurrencyInstance(Locale.ITALY);
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : euro.format(value));
            }
        });


        table.getColumns().setAll(
                guestCol, otaCol, provenanceCol, prenotCol, inCol, outCol, nightsCol, adultGuestsCol, childGuestsCol, amountCol
        );

        table.setItems(rows);
    }


    private void saveReservation() {

        var form = view.getFormPane();

        // --- 1. LETTURA DATI ---

        String guestText = form.getGuestField().getText();
        Ota ota = form.getOtaBox().getValue();
        var checkIn = form.getCheckInPicker().getValue();
        var checkOut = form.getCheckOutPicker().getValue();
        String adultGuestsCountText = form.getAdultGuestsCountField().getText();
        String childGuestCountText = form.getChildGuestsCountField().getText();
        String notes = form.getNotesArea().getText();
        String amountText = form.getAmount().getText();
        String provenance = form.getProvenanceField().getText();
        var createdAt = form.getCreatedAtPicker().getValue();
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        // --- 2. VALIDAZIONE BASE ---

        if (guestText == null || guestText.isBlank()) {
            showError("Cliente mancante");
            return;
        }

        if (ota == null) {
            showError("OTA non selezionata");
            return;
        }

        if (checkOut == null || !checkOut.isAfter(checkIn)) {
            showError("Date non valide");
            return;
        }

        int adultGuestsCount;
        try {
            adultGuestsCount = Integer.parseInt(adultGuestsCountText);
            if (adultGuestsCount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Numero ospiti non valido");
            return;
        }

        int childGuestsCount;
        try {
            childGuestsCount = Integer.parseInt(childGuestCountText);
            if (childGuestsCount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Numero ospiti non valido");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException();
            }
        } catch (Exception e) {
            showError("Importo non valido");
            return;
        }

        // --- 3. PARSE NOME / COGNOME ---

        String[] parts = guestText.trim().split("\\s+", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        // --- 4. INSERT GUEST ---

        var guest = guestRepo.insert(
                new Guest(
                        null,
                        firstName,
                        lastName,
                        null
                )
        );

        // --- 5. INSERT RESERVATION ---

        reservationRepo.insert(
                new Reservation(
                        null,
                        ota.id(),
                        guest.id(),
                        provenance,
                        checkIn,
                        checkOut,
                        nights,
                        adultGuestsCount,
                        childGuestsCount,
                        amount,
                        createdAt,
                        notes
                )
        );

        // --- 6. REFRESH TABLE ---

        loadReservations();

        // --- 7. RESET FORM (OPZIONALE MA CONSIGLIATO) ---

        form.getGuestField().clear();
        form.getCheckInPicker().setValue(null);
        form.getCheckOutPicker().setValue(null);
        form.getAdultGuestsCountField().clear();
        form.getNotesArea().clear();
        form.getOtaBox().setValue(null);
    }

    private void loadReservations() {
        rows.clear();

        for (Reservation r : reservationRepo.findAll()) {

            Guest g = guestRepo.findById(r.guestId());
            Ota o = otaRepo.findById(r.otaId());

            rows.add(new ReservationRow(
                    r.id(),
                    g.firstName() + " " + g.lastName(),
                    o.name(),
                    r.provenance(),
                    r.createdAt(),
                    r.checkIn(),
                    r.checkOut(),
                    r.nights(),
                    r.adultGuestsCount(),
                    r.childGuestsCount(),
                    r.totalAmount()
            ));
        }
    }

    private void showError(String message) {
        // per ora semplice
        System.err.println(message);
    }
}
