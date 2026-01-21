package com.orodent.azdora.feature.contact;

import com.orodent.azdora.core.domain.model.ContactType;
import javafx.beans.property.*;

public class GuestContactRow {

    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<ContactType> type = new SimpleObjectProperty<>();
    private final StringProperty value = new SimpleStringProperty();

    public GuestContactRow(long id, ContactType type, String value) {
        this.id.set(id);
        this.type.set(type);
        this.value.set(value);
    }

    public long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }

    public ContactType getType() { return type.get(); }
    public ObjectProperty<ContactType> typeProperty() { return type; }
    public void setType(ContactType t) { type.set(t); }

    public String getValue() { return value.get(); }
    public StringProperty valueProperty() { return value; }
    public void setValue(String v) { value.set(v); }
}
