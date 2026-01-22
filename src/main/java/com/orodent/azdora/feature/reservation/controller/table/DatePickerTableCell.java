package com.orodent.azdora.feature.reservation.controller.table;

import com.orodent.azdora.feature.reservation.view.model.ReservationRow;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

import java.time.LocalDate;

public class DatePickerTableCell extends TableCell<ReservationRow, LocalDate> {

    private final DatePicker datePicker = new DatePicker();

    public DatePickerTableCell() {
        datePicker.setOnAction(e -> commitEdit(datePicker.getValue()));

        setGraphic(datePicker);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(LocalDate value, boolean empty) {
        super.updateItem(value, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(value == null ? "" : value.toString());
            datePicker.setValue(value);
            setGraphic(datePicker);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }
}
