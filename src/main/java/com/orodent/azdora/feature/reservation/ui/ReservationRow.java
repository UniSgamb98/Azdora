package com.orodent.azdora.feature.reservation.ui;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationRow {

    private final LongProperty id = new SimpleLongProperty();

    private final StringProperty guestName = new SimpleStringProperty();
    private final StringProperty otaName = new SimpleStringProperty();
    private final StringProperty provenance = new SimpleStringProperty();

    private final ObjectProperty<LocalDate> checkIn = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> checkOut = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> prenotatoIl = new SimpleObjectProperty<>();
    private final LongProperty nightsCount = new SimpleLongProperty();

    private final IntegerProperty adultGuestsCount = new SimpleIntegerProperty();
    private final IntegerProperty childGuestsCount = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();

    public ReservationRow(
            long id,
            String guestName,
            String otaName,
            String provenance,
            LocalDate prenot,
            LocalDate checkIn,
            LocalDate checkOut,
            Long nights,
            int adultGuestsCount,
            int childGuestsCount,
            BigDecimal amount
    ) {
        this.id.set(id);
        this.guestName.set(guestName);
        this.otaName.set(otaName);
        this.checkIn.set(checkIn);
        this.checkOut.set(checkOut);
        this.adultGuestsCount.set(adultGuestsCount);
        this.amount.set(amount);
        this.provenance.set(provenance);
        this.prenotatoIl.set(prenot);
        this.nightsCount.set(nights);
        this.childGuestsCount.set(childGuestsCount);
    }

    public long getId() { return id.get(); }
    public StringProperty guestProperty() { return guestName; }
    public StringProperty otaProperty() { return otaName; }
    public StringProperty provenanceProperty() { return provenance; }
    public ObjectProperty<LocalDate> checkInProperty() { return checkIn; }
    public ObjectProperty<LocalDate> checkOutProperty() { return checkOut; }
    public LongProperty nightsCountProperty() { return nightsCount; }
    public ObjectProperty<LocalDate> prenotProperty() { return prenotatoIl; }
    public IntegerProperty adultGuestsProperty() { return adultGuestsCount; }
    public IntegerProperty childGuestsProperty() { return childGuestsCount; }
    public ObjectProperty<BigDecimal> amountProperty() { return amount; }

    public void setProvenance(String newProvenance) { provenance.set(newProvenance); }
    public void setAdultGuestsCount(int newAdultGuestsCount) { adultGuestsCount.set(newAdultGuestsCount); }
    public void setChildGuestsCount(int newChildGuestsCount) { childGuestsCount.set(newChildGuestsCount); }
    public void setAmount(BigDecimal newAmount) { amount.set(newAmount); }
    public void setCheckIn(LocalDate newCheckIn) {
        checkInProperty().set(newCheckIn);
        nightsCount.set(ChronoUnit.DAYS.between(newCheckIn, checkOut.get()));
    }
    public void setCheckOut(LocalDate newCheckOut) {
        checkOut.set(newCheckOut);
        nightsCount.set(ChronoUnit.DAYS.between(checkIn.get(), newCheckOut));
    }

    public LocalDate getCheckOut() { return checkOut.get(); }
    public LocalDate getCheckIn() { return checkIn.get(); }
}
