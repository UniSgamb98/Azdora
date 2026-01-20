package com.orodent.azdora.feature.reservation.controller;

import com.orodent.azdora.core.database.model.Guest;
import com.orodent.azdora.feature.reservation.service.GuestService;
import com.orodent.azdora.feature.reservation.view.partials.GuestSearchPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class GuestSearchController {

    private final GuestSearchPane view;
    private final GuestService guestService;

    private final ObservableList<Guest> guests = FXCollections.observableArrayList();
    private boolean autoCompleting = false;

    private Consumer<Guest> onGuestSelected = g -> {};

    public GuestSearchController(GuestSearchPane view, GuestService guestService) {
        this.view = view;
        this.guestService = guestService;
        init();
    }

    public void setOnGuestSelected(Consumer<Guest> onGuestSelected) {
        this.onGuestSelected = onGuestSelected;
    }

    private void init() {
        view.getGuestList().setItems(guests);

        view.getGuestList().setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Guest item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.firstName() + " " + item.lastName()));
            }
        });

        // carico iniziale (lista completa, limitata)
        guests.setAll(guestService.search("", 50));

        view.getGuestList().getSelectionModel().selectedItemProperty().addListener((obs, old, g) -> {
            if (g != null) onGuestSelected.accept(g);
        });

        view.getSearchField().textProperty().addListener((obs, oldText, newText) -> {
            if (autoCompleting) return;

            // 1) aggiorna lista (mentre scrivi)
            guests.setAll(guestService.search(newText, 50));

            // 2) autocomplete col primo best match (solo se non stai cancellando)
            boolean isDeleting = newText != null && oldText != null && newText.length() < oldText.length();
            if (isDeleting) return;

            Optional<Guest> best = guestService.bestMatch(newText);
            if (best.isEmpty()) return;

            String suggestion = best.get().firstName() + " " + best.get().lastName();

            if (suggestion.length() > Objects.requireNonNull(newText).length() && startsWithIgnoreCase(suggestion, newText)) {
                autoCompleting = true;
                view.getSearchField().setText(suggestion);

                // caret + selezione della parte completata
                int from = newText.length();
                Platform.runLater(() -> {
                    view.getSearchField().positionCaret(from);
                    view.getSearchField().selectRange(from, suggestion.length());
                    autoCompleting = false;
                });
            }
        });
    }

    private static boolean startsWithIgnoreCase(String full, String prefix) {
        if (prefix == null) return false;
        if (prefix.isBlank()) return false;
        return full.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
