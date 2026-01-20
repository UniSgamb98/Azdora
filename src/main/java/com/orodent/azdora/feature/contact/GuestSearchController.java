package com.orodent.azdora.feature.contact;

import com.orodent.azdora.core.database.model.ContactType;
import com.orodent.azdora.core.database.model.Guest;
import com.orodent.azdora.core.database.model.GuestContact;
import com.orodent.azdora.feature.reservation.view.partials.GuestSearchPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.Optional;
import java.util.function.Consumer;

public class GuestSearchController {

    private final GuestSearchPane view;
    private final GuestService guestService;
    private final GuestContactService contactService;

    private final ObservableList<Guest> guestList = FXCollections.observableArrayList();
    private final ObservableList<GuestContactRow> contactRows = FXCollections.observableArrayList();

    private Consumer<Guest> onGuestSelected = g -> {};

    public GuestSearchController(GuestSearchPane view, GuestService guestService, GuestContactService contactService) {
        this.view = view;
        this.guestService = guestService;
        this.contactService = contactService;

        init();
    }

    public void setOnGuestSelected(Consumer<Guest> onGuestSelected) {
        this.onGuestSelected = onGuestSelected;
    }

    private void init() {
        // lista guest
        view.getGuestList().setItems(guestList);
        view.getGuestList().setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Guest item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.firstName() + " " + item.lastName());
            }
        });

        // tabella contatti
        initContactsTable();
        view.getContactsTable().setItems(contactRows);

        // dati iniziali guest
        guestList.setAll(guestService.search("", 50));

        // selezione guest -> carica notes + contatti
        view.getGuestList().getSelectionModel().selectedItemProperty().addListener((obs, old, g) -> {
            if (g == null) {
                view.getNotesArea().clear();
                contactRows.clear();
                return;
            }

            view.getNotesArea().setText(g.notes() == null ? "" : g.notes());
            loadContacts(g.id());

            onGuestSelected.accept(g);
        });

        // salva note
        view.getSaveNotesButton().setOnAction(e -> {
            Guest g = view.getGuestList().getSelectionModel().getSelectedItem();
            if (g == null) return;

            String notes = view.getNotesArea().getText();
            guestService.updateNotes(g.id(), notes);

            // opzionale: aggiorna anche l’oggetto Guest in memoria se vuoi mantenerlo sync
            // (dipende da come è fatto Guest: record immutabile => ricarichi lista se necessario)
        });

        // ricerca + autocomplete (logica tua già presente) -> qui mantieni la tua, ma al posto di filtro locale:
        view.getSearchField().textProperty().addListener((obs, oldText, newText) -> {
            guestList.setAll(guestService.search(newText, 50));

            Optional<Guest> best = guestService.bestMatch(newText);
            best.ifPresent(b -> {
                // se vuoi autocomplete nel field, lo gestisci come già avevi (con flag per evitare loop)
                // qui ho lasciato volutamente minimal.
            });
        });

        // aggiungi contatto
        view.getAddContactButton().setOnAction(e -> {
            Guest g = view.getGuestList().getSelectionModel().getSelectedItem();
            if (g == null) return;

            // default: EMAIL vuota (o il primo enum)
            ContactType type = ContactType.values()[0];
            GuestContact created = contactService.create(g.id(), type, "");
            contactRows.add(new GuestContactRow(created.id(), created.contactType(), created.contactValue()));

            view.getContactsTable().getSelectionModel().selectLast();
            view.getContactsTable().scrollTo(contactRows.size() - 1);
        });

        // rimuovi contatto
        view.getRemoveContactButton().setOnAction(e -> {
            GuestContactRow sel = view.getContactsTable().getSelectionModel().getSelectedItem();
            if (sel == null) return;

            contactService.delete(sel.getId());
            contactRows.remove(sel);
        });
    }

    private void initContactsTable() {
        TableView<GuestContactRow> table = view.getContactsTable();
        table.setEditable(true);

        TableColumn<GuestContactRow, ContactType> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(d -> d.getValue().typeProperty());
        typeCol.setCellFactory(ComboBoxTableCell.forTableColumn(ContactType.values()));
        typeCol.setOnEditCommit(e -> {
            GuestContactRow row = e.getRowValue();
            ContactType old = e.getOldValue();
            ContactType val = e.getNewValue();

            try {
                row.setType(val);
                contactService.updateType(row.getId(), val);
            } catch (Exception ex) {
                row.setType(old);
                table.refresh();
            }
        });

        TableColumn<GuestContactRow, String> valueCol = new TableColumn<>("Valore");
        valueCol.setCellValueFactory(d -> d.getValue().valueProperty());
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valueCol.setOnEditCommit(e -> {
            GuestContactRow row = e.getRowValue();
            String old = e.getOldValue();
            String val = e.getNewValue();

            try {
                row.setValue(val);
                contactService.updateValue(row.getId(), val);
            } catch (Exception ex) {
                row.setValue(old);
                table.refresh();
            }
        });

        table.getColumns().setAll(typeCol, valueCol);
    }

    private void loadContacts(long guestId) {
        contactRows.setAll(
                contactService.findByGuestId(guestId).stream()
                        .map(c -> new GuestContactRow(c.id(), c.contactType(), c.contactValue()))
                        .toList()
        );
    }
}
