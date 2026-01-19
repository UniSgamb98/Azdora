package com.orodent.azdora.feature.reservation.view.partials;

import com.orodent.azdora.core.database.model.Ota;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ReservationFormPane extends GridPane {

    private final TextField guestField = new TextField();
    private final ComboBox<Ota> otaBox = new ComboBox<>();
    private final DatePicker checkInPicker = new DatePicker();
    private final DatePicker checkOutPicker = new DatePicker();
    private final DatePicker createdAtPicker = new DatePicker();
    private final TextField adultGuestsCountField = new TextField();
    private final TextField childGuestsCountField = new TextField();
    private final TextField provenanceField = new TextField();
    private final TextArea notesArea = new TextArea();
    private final Button saveButton = new Button("Aggiungi prenotazione");
    private final TextField amount = new TextField();

    public ReservationFormPane() {
        setPadding(new Insets(10));
        setHgap(10);
        setVgap(8);

        int r = 0;

        add(new Label("Cliente"), 0, r);
        add(guestField, 1, r);

        add(new Label("OTA"), 2, r);
        add(otaBox, 3, r++);

        add(new Label("Check-in"), 0, r);
        add(checkInPicker, 1, r);

        add(new Label("Check-out"), 2, r);
        add(checkOutPicker, 3, r++);

        add(new Label("Adulti"), 0, r);
        add(adultGuestsCountField, 1, r);

        add(new Label("Bambini"), 2, r);
        add(childGuestsCountField, 3, r++);

        add(new Label("Prenotato il"), 0, r);
        add(createdAtPicker, 1, r);

        add(new Label("Provenienza Ospite"), 2, r);
        add(provenanceField, 3, r++);

        add(new Label("Totale euro"), 0, r);
        add(amount, 1, r++, 3, 1);

        add(new Label("Note"), 0, r);
        add(notesArea, 1, r++, 3, 2);

        r += 2;
        HBox actions = new HBox(saveButton);
        add(actions, 2, r);
    }

    public void clear() {
        guestField.clear();
        otaBox.setValue(null);
        checkInPicker.setValue(null);
        checkOutPicker.setValue(null);
        createdAtPicker.setValue(null);
        adultGuestsCountField.clear();
        childGuestsCountField.clear();
        provenanceField.clear();
        amount.clear();
        notesArea.clear();
    }

    // --- GETTERS ---
    public TextField getGuestField() {
        return guestField;
    }

    public ComboBox<Ota> getOtaBox() {
        return otaBox;
    }

    public TextField getProvenanceField() {
        return provenanceField;
    }

    public DatePicker getCheckInPicker() {
        return checkInPicker;
    }

    public DatePicker getCheckOutPicker() {
        return checkOutPicker;
    }

    public DatePicker getCreatedAtPicker() {
        return createdAtPicker;
    }

    public TextField getAdultGuestsCountField() {
        return adultGuestsCountField;
    }

    public TextField getChildGuestsCountField() {
        return childGuestsCountField;
    }

    public TextArea getNotesArea() {
        return notesArea;
    }

    public TextField getAmount() {
        return amount;
    }

    public Button getSaveButton() {
        return saveButton;
    }
}
