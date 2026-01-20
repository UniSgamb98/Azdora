package com.orodent.azdora.feature.reservation.view;

import com.orodent.azdora.feature.reservation.ui.ReservationRow;
import com.orodent.azdora.feature.reservation.view.partials.GuestSearchPane;
import com.orodent.azdora.feature.reservation.view.partials.ReservationFormPane;
import com.orodent.azdora.feature.reservation.view.partials.ReservationSummaryPane;
import javafx.geometry.Pos;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class ReservationMainView extends BorderPane {

    private final TableView<ReservationRow> table = new TableView<>();
    private final ReservationFormPane formPane = new ReservationFormPane();
    private final ReservationSummaryPane summaryPane = new ReservationSummaryPane();
    private final GuestSearchPane guestSearchPane = new GuestSearchPane();

    public ReservationMainView() {
        setCenter(table);
        setBottom(formPane);
        setLeft(summaryPane);
        setRight(guestSearchPane);

        formPane.setAlignment(Pos.CENTER);
    }

    public TableView<ReservationRow> getTable() {
        return table;
    }
    public ReservationFormPane getFormPane() {
        return formPane;
    }
    public ReservationSummaryPane getSummaryPane() {
        return summaryPane;
    }
    public GuestSearchPane getGuestSearchPane() {
        return guestSearchPane;
    }
}
