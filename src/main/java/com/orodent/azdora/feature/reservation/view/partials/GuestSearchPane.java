package com.orodent.azdora.feature.reservation.view.partials;

import com.orodent.azdora.core.database.model.Guest;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class GuestSearchPane extends VBox {

    private final Label title = new Label("Clienti");
    private final ListView<Guest> guestList = new ListView<>();
    private final TextField searchField = new TextField();

    public GuestSearchPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(280);

        searchField.setPromptText("Cerca cliente...");

        // Se vuoi un'altezza fissa per la lista (facoltativo)
        guestList.setPrefHeight(300);

        getChildren().addAll(
                title,
                guestList,
                searchField
        );
    }

    public ListView<Guest> getGuestList() {
        return guestList;
    }

    public TextField getSearchField() {
        return searchField;
    }
}
