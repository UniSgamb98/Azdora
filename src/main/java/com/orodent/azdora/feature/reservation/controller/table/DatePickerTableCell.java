package com.orodent.azdora.feature.reservation.controller.table;

import com.orodent.azdora.feature.reservation.view.model.ReservationRow;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerTableCell extends TableCell<ReservationRow, LocalDate> {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DatePicker datePicker = new DatePicker();

    public DatePickerTableCell() {
        datePicker.setOnAction(e -> commitEdit(datePicker.getValue()));

        setGraphic(datePicker);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    public static LocalDate parseDateInput(String text) {
        if (text == null) {
            return null;
        }
        String digits = text.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return null;
        }

        if (digits.length() > 6) {
            digits = digits.substring(0, 6);
        }

        LocalDate now = LocalDate.now();
        int day;
        int month = now.getMonthValue();
        int year = now.getYear() % 100;

        if (digits.length() >= 2) {
            day = Integer.parseInt(digits.substring(0, 2));
        } else {
            day = Integer.parseInt(digits);
        }

        if (digits.length() >= 4) {
            month = Integer.parseInt(digits.substring(2, 4));
        } else if (digits.length() == 3) {
            month = Integer.parseInt(digits.substring(2, 3));
        }

        if (digits.length() >= 6) {
            year = Integer.parseInt(digits.substring(4, 6));
        } else if (digits.length() == 5) {
            year = Integer.parseInt(digits.substring(4, 5));
        }

        return LocalDate.of(2000 + year, month, day);
    }

    @Override
    protected void updateItem(LocalDate value, boolean empty) {
        super.updateItem(value, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(value == null ? "" : value.format(DISPLAY_FORMAT));
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
