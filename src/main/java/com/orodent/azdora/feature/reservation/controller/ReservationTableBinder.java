package com.orodent.azdora.feature.reservation.controller;

import com.orodent.azdora.core.domain.model.Ota;
import com.orodent.azdora.feature.reservation.controller.table.DatePickerTableCell;
import com.orodent.azdora.feature.reservation.service.ReservationService;
import com.orodent.azdora.feature.reservation.view.model.ReservationRow;
import javafx.collections.FXCollections;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.DatePicker;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Consumer;

public class ReservationTableBinder {

    private final ReservationService service;
    private final Consumer<String> onError;

    public ReservationTableBinder(ReservationService service, Consumer<String> onError) {
        this.service = service;
        this.onError = onError;
    }

    public void bind(TableView<ReservationRow> table) {
        table.setEditable(true);
        table.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                handleHorizontalNavigation(table, 1);
                event.consume();
            } else if (event.getCode() == KeyCode.RIGHT) {
                handleHorizontalNavigation(table, 1);
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                handleHorizontalNavigation(table, -1);
                event.consume();
            } else if (event.getCode() == KeyCode.UP) {
                handleVerticalNavigation(table, -1);
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                handleVerticalNavigation(table, 1);
                event.consume();
            }
        });

        TableColumn<ReservationRow, String> guestCol = new TableColumn<>("Cliente");
        guestCol.setCellValueFactory(d -> d.getValue().guestProperty());
        guestCol.setCellFactory(TextFieldTableCell.forTableColumn());
        guestCol.setOnEditCommit(event -> {
            ReservationRow row = event.getRowValue();
            String oldValue = event.getOldValue();
            String newValue = event.getNewValue();

            if (isNewRow(row)) {
                row.setGuestName(newValue);
                return;
            }

            row.setGuestName(oldValue);
            table.refresh();
        });

        TableColumn<ReservationRow, Ota> otaCol = new TableColumn<>("OTA");
        otaCol.setCellValueFactory(d -> d.getValue().otaProperty());
        otaCol.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(service.loadOtas())
        ));
        otaCol.setOnEditCommit(event -> {
            ReservationRow row = event.getRowValue();
            Ota oldValue = event.getOldValue();
            Ota newValue = event.getNewValue();

            try {
                row.setOta(newValue);
                if (!isNewRow(row)) {
                    service.updateOta(row.getId(), newValue);
                }
            } catch (Exception ex) {
                row.setOta(oldValue);
                table.refresh();
                onError.accept(ex.getMessage());
            }
        });

        TableColumn<ReservationRow, LocalDate> prenotCol = new TableColumn<>("Prenotato il");
        prenotCol.setCellValueFactory(d -> d.getValue().prenotProperty());
        prenotCol.setCellFactory(col -> new DatePickerTableCell());
        prenotCol.setOnEditCommit(event -> {
            ReservationRow row = event.getRowValue();
            LocalDate oldValue = event.getOldValue();
            LocalDate newValue = event.getNewValue();

            if (isNewRow(row)) {
                row.setPrenotatoIl(newValue);
                return;
            }

            row.setPrenotatoIl(oldValue);
            table.refresh();
        });

        TableColumn<ReservationRow, String> provenanceCol = new TableColumn<>("Provenienza");
        provenanceCol.setCellValueFactory(d -> d.getValue().provenanceProperty());
        provenanceCol.setCellFactory(TextFieldTableCell.forTableColumn());
        provenanceCol.setOnEditCommit(event -> {
            ReservationRow row = event.getRowValue();
            String oldValue = event.getOldValue();
            String newValue = event.getNewValue();

            try {
                row.setProvenance(newValue);
                if (!isNewRow(row)) {
                    service.updateProvenance(row.getId(), newValue);
                }
            } catch (Exception ex) {
                row.setProvenance(oldValue);
                table.refresh();
                onError.accept(ex.getMessage());
            }
        });

        TableColumn<ReservationRow, String> notesCol = new TableColumn<>("Note");
        notesCol.setCellValueFactory(d -> d.getValue().notesProperty());
        notesCol.setCellFactory(TextFieldTableCell.forTableColumn());
        notesCol.setOnEditCommit(event -> {
            ReservationRow row = event.getRowValue();
            String oldValue = event.getOldValue();
            String newValue = event.getNewValue();

            try {
                row.setNotes(newValue);
                if (!isNewRow(row)) {
                    service.updateNotes(row.getId(), newValue);
                }
            } catch (Exception ex) {
                row.setNotes(oldValue);
                table.refresh();
                onError.accept(ex.getMessage());
            }
        });

        TableColumn<ReservationRow, Long> nightsCol = new TableColumn<>("notti");
        nightsCol.setCellValueFactory(d -> d.getValue().nightsCountProperty().asObject());
        nightsCol.setEditable(false);

        TableColumn<ReservationRow, LocalDate> inCol = new TableColumn<>("Check-in");
        inCol.setCellValueFactory(d -> d.getValue().checkInProperty());
        inCol.setCellFactory(col -> new DatePickerTableCell());
        inCol.setOnEditCommit(e -> {
            ReservationRow row = e.getRowValue();
            LocalDate oldValue = e.getOldValue();
            LocalDate newValue = e.getNewValue();

            try {
                row.setCheckIn(newValue);
                if (!isNewRow(row)) {
                    service.updateDates(row.getId(), row.getCheckIn(), row.getCheckOut());
                }
            } catch (Exception ex) {
                row.setCheckIn(oldValue);
                table.refresh();
                onError.accept(ex.getMessage());
            }
        });

        TableColumn<ReservationRow, LocalDate> outCol = new TableColumn<>("Check-out");
        outCol.setCellValueFactory(d -> d.getValue().checkOutProperty());
        outCol.setCellFactory(col -> new DatePickerTableCell());
        outCol.setOnEditCommit(e -> {
            ReservationRow row = e.getRowValue();
            LocalDate oldValue = e.getOldValue();
            LocalDate newValue = e.getNewValue();

            try {
                row.setCheckOut(newValue);
                if (!isNewRow(row)) {
                    service.updateDates(row.getId(), row.getCheckIn(), row.getCheckOut());
                }
            } catch (Exception ex) {
                row.setCheckOut(oldValue);
                table.refresh();
                onError.accept(ex.getMessage());
            }
        });

        TableColumn<ReservationRow, Integer> adultGuestsCol = new TableColumn<>("Adulti");
        adultGuestsCol.setCellValueFactory(d -> d.getValue().adultGuestsProperty().asObject());
        adultGuestsCol.setCellFactory(col -> new TextFieldTableCell<>(new IntegerStringConverter()) {
            @Override
            public void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                ReservationRow row = getTableRow() != null ? getTableRow().getItem() : null;
                if (!empty && row != null && isNewRow(row) && value != null && value == 0) {
                    setText("");
                }
            }
        });
        adultGuestsCol.setOnEditCommit(event -> {
            ReservationRow row = event.getRowValue();
            Integer oldValue = event.getOldValue();
            Integer newValue = event.getNewValue();

            try {
                row.setAdultGuestsCount(newValue);
                if (!isNewRow(row)) {
                    service.updateAdultGuests(row.getId(), newValue);
                }
            } catch (Exception ex) {
                row.setAdultGuestsCount(oldValue);
                table.refresh();
                onError.accept(ex.getMessage());
            }
        });

        TableColumn<ReservationRow, Integer> childGuestsCol = new TableColumn<>("Bambini");
        childGuestsCol.setCellValueFactory(d -> d.getValue().childGuestsProperty().asObject());
        childGuestsCol.setCellFactory(col -> new TextFieldTableCell<>(new IntegerStringConverter()) {
            @Override
            public void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                ReservationRow row = getTableRow() != null ? getTableRow().getItem() : null;
                if (!empty && row != null && isNewRow(row) && value != null && value == 0) {
                    setText("");
                }
            }
        });
        childGuestsCol.setOnEditCommit(event -> {
            ReservationRow row = event.getRowValue();
            Integer oldValue = event.getOldValue();
            Integer newValue = event.getNewValue();

            try {
                row.setChildGuestsCount(newValue);
                if (!isNewRow(row)) {
                    service.updateChildGuests(row.getId(), newValue);
                }
            } catch (Exception ex) {
                row.setChildGuestsCount(oldValue);
                table.refresh();
                onError.accept(ex.getMessage());
            }
        });

        TableColumn<ReservationRow, BigDecimal> amountCol = new TableColumn<>("Fatturato");
        amountCol.setCellValueFactory(d -> d.getValue().amountProperty());
        amountCol.setCellFactory(col -> new TextFieldTableCell<>(new BigDecimalStringConverter()) {
            private final NumberFormat euro = NumberFormat.getCurrencyInstance(Locale.ITALY);
            private final BigDecimalStringConverter converter = new BigDecimalStringConverter();

            @Override
            public void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : euro.format(value));
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (getItem() != null) setText(converter.toString(getItem()));
            }
        });
        amountCol.setOnEditCommit(e -> {
            ReservationRow row = e.getRowValue();
            BigDecimal oldValue = e.getOldValue();
            BigDecimal newValue = e.getNewValue();

            try {
                row.setAmount(newValue);
                if (!isNewRow(row)) {
                    service.updateAmount(row.getId(), newValue);
                }
            } catch (Exception ex) {
                row.setAmount(oldValue);
                table.refresh();
                onError.accept(ex.getMessage());
            }
        });

        table.getColumns().setAll(
                guestCol, otaCol, provenanceCol, prenotCol,
                inCol, outCol, nightsCol,
                adultGuestsCol, childGuestsCol, amountCol, notesCol
        );
    }

    private boolean isNewRow(ReservationRow row) {
        return row != null && row.getId() <= 0;
    }

    private void handleHorizontalNavigation(TableView<ReservationRow> table, int direction) {
        TablePosition<ReservationRow, ?> position = table.getEditingCell();
        if (position == null) {
            position = table.getFocusModel().getFocusedCell();
        }
        if (position == null) {
            return;
        }

        if (table.getEditingCell() != null && !commitEditingCell(table, position)) {
            return;
        }

        int rowIndex = position.getRow();
        var columns = table.getVisibleLeafColumns();
        int columnIndex = table.getVisibleLeafIndex(position.getTableColumn());
        if (columns.isEmpty() || columnIndex < 0) {
            return;
        }

        int nextColumnIndex = columnIndex;
        TableColumn<ReservationRow, ?> nextColumn = null;
        for (int i = 0; i < columns.size(); i++) {
            nextColumnIndex = (nextColumnIndex + direction + columns.size()) % columns.size();
            TableColumn<ReservationRow, ?> candidate = columns.get(nextColumnIndex);
            if (candidate.isEditable()) {
                nextColumn = candidate;
                break;
            }
        }
        if (nextColumn == null) {
            return;
        }
        table.getSelectionModel().clearAndSelect(rowIndex, nextColumn);
        table.edit(rowIndex, nextColumn);
    }

    private void handleVerticalNavigation(TableView<ReservationRow> table, int direction) {
        TablePosition<ReservationRow, ?> position = table.getEditingCell();
        if (position == null) {
            position = table.getFocusModel().getFocusedCell();
        }
        if (position == null) {
            return;
        }

        if (table.getEditingCell() != null && !commitEditingCell(table, position)) {
            return;
        }

        int rowIndex = position.getRow() + direction;
        if (rowIndex < 0 || rowIndex >= table.getItems().size()) {
            return;
        }

        TableColumn<ReservationRow, ?> column = position.getTableColumn();
        table.getSelectionModel().clearAndSelect(rowIndex, column);
        if (column != null && column.isEditable()) {
            table.edit(rowIndex, column);
        }
    }

    private boolean commitEditingCell(TableView<ReservationRow> table, TablePosition<ReservationRow, ?> position) {
        Object cell = table.queryAccessibleAttribute(
                AccessibleAttribute.CELL_AT_ROW_COLUMN,
                position.getRow(),
                table.getVisibleLeafIndex(position.getTableColumn())
        );
        if (!(cell instanceof javafx.scene.control.TableCell<?, ?> tableCell)) {
            return true;
        }

        if (!tableCell.isEditing()) {
            return true;
        }

        Object newValue = null;
        Object item = tableCell.getItem();
        var graphic = tableCell.getGraphic();

        try {
            if (graphic instanceof TextField textField) {
                String text = textField.getText();
                if (item instanceof Integer) {
                    newValue = text == null || text.trim().isEmpty() ? item : Integer.valueOf(text.trim());
                } else if (item instanceof BigDecimal) {
                    newValue = text == null || text.trim().isEmpty() ? item : new BigDecimal(text.trim());
                } else {
                    newValue = text == null ? "" : text;
                }
            } else if (graphic instanceof DatePicker datePicker) {
                String editorText = datePicker.getEditor() != null ? datePicker.getEditor().getText() : null;
                if (editorText != null && !editorText.isBlank()) {
                    datePicker.setValue(DatePickerTableCell.parseDateInput(editorText));
                }
                newValue = datePicker.getValue();
            } else if (graphic instanceof ComboBox<?> comboBox) {
                newValue = comboBox.getValue();
            } else {
                newValue = item;
            }
        } catch (RuntimeException ex) {
            onError.accept("Valore non valido");
            return false;
        }

        ((javafx.scene.control.TableCell<Object, Object>) tableCell).commitEdit(newValue);
        return true;
    }
}
