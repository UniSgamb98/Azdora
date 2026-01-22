package com.orodent.azdora.feature.reservation.view;

import com.orodent.azdora.feature.reservation.view.model.ReservationRow;
import com.orodent.azdora.feature.contact.view.GuestSearchPane;
import com.orodent.azdora.feature.reservation.view.partials.ReservationSummaryPane;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;

public class ReservationMainView extends BorderPane {

    private final TableView<ReservationRow> table = new TableView<>();
    private final ReservationSummaryPane summaryPane = new ReservationSummaryPane();
    private final GuestSearchPane guestSearchPane = new GuestSearchPane();
    private final Button addReservationButton = new Button("Aggiungi prenotazione");
    private final Button cancelReservationButton = new Button("Annulla");
    private final HBox newReservationActions = new HBox(10, addReservationButton, cancelReservationButton);

    public ReservationMainView() {
        setCenter(table);
        setBottom(newReservationActions);
        setLeft(summaryPane);
        setRight(guestSearchPane);

        newReservationActions.setAlignment(Pos.CENTER_RIGHT);
        newReservationActions.setVisible(false);
        newReservationActions.setManaged(false);
    }

    public TableView<ReservationRow> getTable() {
        return table;
    }
    public ReservationSummaryPane getSummaryPane() {
        return summaryPane;
    }
    public GuestSearchPane getGuestSearchPane() {
        return guestSearchPane;
    }

    public Button getAddReservationButton() {
        return addReservationButton;
    }

    public Button getCancelReservationButton() {
        return cancelReservationButton;
    }

    public HBox getNewReservationActions() {
        return newReservationActions;
    }
}
