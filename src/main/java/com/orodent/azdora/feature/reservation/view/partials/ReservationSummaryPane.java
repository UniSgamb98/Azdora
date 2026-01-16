package com.orodent.azdora.feature.reservation.view.partials;

import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ReservationSummaryPane extends VBox {

    private final ChoiceBox<Integer> yearBox = new ChoiceBox<>();
    private final Label revenueLabel = new Label();
    private final Label totalGuestsLabel = new Label();
    private final Label newGuestsLabel = new Label();
    private final Label returningGuestsLabel = new Label();

    public ReservationSummaryPane() {
        setSpacing(10);
        setPadding(new Insets(15));

        getChildren().addAll(
                new Label("Riepilogo annuale"),
                yearBox,
                revenueLabel,
                totalGuestsLabel,
                newGuestsLabel,
                returningGuestsLabel
        );
    }

    public ChoiceBox<Integer> getYearBox() {
        return yearBox;
    }
    public Label getNewGuestsLabel() {
        return newGuestsLabel;
    }
    public Label getReturningGuestsLabel() {
        return returningGuestsLabel;
    }
    public Label getRevenueLabel() {
        return revenueLabel;
    }
    public Label getTotalGuestsLabel() {
        return totalGuestsLabel;
    }
}

