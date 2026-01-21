package com.orodent.azdora.feature.contact.controller;

import com.orodent.azdora.core.domain.model.ContactType;
import com.orodent.azdora.core.domain.model.Guest;
import com.orodent.azdora.core.domain.model.GuestContact;
import com.orodent.azdora.feature.contact.GuestContactRow;
import com.orodent.azdora.feature.contact.service.GuestContactService;
import com.orodent.azdora.feature.contact.service.GuestService;
import com.orodent.azdora.feature.contact.view.GuestSearchPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.Node;
import javafx.scene.Parent;

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

        wireFocusReset();

        // selezione guest -> carica notes + contatti
        view.getGuestList().getSelectionModel().selectedItemProperty().addListener((obs, old, g) -> {
            if (g == null) {
                setDetailsVisible(false);
                setGuestListVisible(true);
                view.getNotesArea().clear();
                contactRows.clear();
                return;
            }

            setDetailsVisible(true);
            setGuestListVisible(false);
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
            setDetailsVisible(false);
            setGuestListVisible(true);

            Optional<Guest> best = guestService.bestMatch(newText);
            best.ifPresent(b -> {
                // se vuoi autocomplete nel field, lo gestisci come già avevi (con flag per evitare loop)
                // qui ho lasciato volutamente minimal.
            });
        });

        view.getSearchField().setOnAction(e -> {
            if (guestList.size() == 1) {
                view.getGuestList().getSelectionModel().select(0);
                view.getGuestList().requestFocus();
            }
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

        setDetailsVisible(false);
        setGuestListVisible(true);
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

    private void setDetailsVisible(boolean visible) {
        view.getNotesLabel().setVisible(visible);
        view.getNotesLabel().setManaged(visible);
        view.getNotesArea().setVisible(visible);
        view.getNotesArea().setManaged(visible);
        view.getSaveNotesButton().setVisible(visible);
        view.getSaveNotesButton().setManaged(visible);
        view.getContactsLabel().setVisible(visible);
        view.getContactsLabel().setManaged(visible);
        view.getContactsTable().setVisible(visible);
        view.getContactsTable().setManaged(visible);
        view.getAddContactButton().setVisible(visible);
        view.getAddContactButton().setManaged(visible);
        view.getRemoveContactButton().setVisible(visible);
        view.getRemoveContactButton().setManaged(visible);
    }

    private void setGuestListVisible(boolean visible) {
        view.getGuestList().setVisible(visible);
        view.getGuestList().setManaged(visible);
    }

    private void wireFocusReset() {
        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) return;
            newScene.focusOwnerProperty().addListener((focusObs, oldFocus, newFocus) -> {
                if (newFocus == null || isDescendantOfView(newFocus)) {
                    return;
                }
                view.getGuestList().getSelectionModel().clearSelection();
                setDetailsVisible(false);
                setGuestListVisible(true);
            });
        });
    }

    private boolean isDescendantOfView(Node node) {
        if (node == view) {
            return true;
        }
        Parent parent = node.getParent();
        while (parent != null) {
            if (parent == view) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}
