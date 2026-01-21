package com.orodent.azdora.feature.reservation.view.partials;

import com.orodent.azdora.core.database.model.Guest;
import com.orodent.azdora.feature.contact.GuestContactRow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuestSearchPane extends VBox {

    private final Label title = new Label("Clienti");

    private final ListView<Guest> guestList = new ListView<>();
    private final TextField searchField = new TextField();

    private final Label notesLabel = new Label("Note");
    private final TextArea notesArea = new TextArea();
    private final Button saveNotesButton = new Button("Salva note");

    private final Label contactsLabel = new Label("Contatti");
    private final TableView<GuestContactRow> contactsTable = new TableView<>();
    private final Button addContactButton = new Button("Aggiungi");
    private final Button removeContactButton = new Button("Rimuovi");

    public GuestSearchPane() {
        setSpacing(10);
        setPadding(new Insets(12));
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(320);

        searchField.setPromptText("Cerca cliente...");

        guestList.setPrefHeight(240);

        notesArea.setPromptText("Note del cliente...");
        notesArea.setWrapText(true);
        notesArea.setPrefRowCount(4);

        contactsTable.setPrefHeight(220);
        contactsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox contactButtons = new HBox(8, addContactButton, removeContactButton);
        contactButtons.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(
                title,
                searchField,
                guestList,
                notesLabel,
                notesArea,
                saveNotesButton,
                contactsLabel,
                contactsTable,
                contactButtons
        );
    }

    public ListView<Guest> getGuestList() { return guestList; }
    public TextField getSearchField() { return searchField; }

    public TextArea getNotesArea() { return notesArea; }
    public Label getNotesLabel() { return notesLabel; }
    public Button getSaveNotesButton() { return saveNotesButton; }

    public Label getContactsLabel() { return contactsLabel; }
    public TableView<GuestContactRow> getContactsTable() { return contactsTable; }
    public Button getAddContactButton() { return addContactButton; }
    public Button getRemoveContactButton() { return removeContactButton; }
}
