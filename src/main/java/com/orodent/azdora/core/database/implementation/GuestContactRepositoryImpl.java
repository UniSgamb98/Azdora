package com.orodent.azdora.core.database.implementation;

import com.orodent.azdora.core.database.model.GuestContact;
import com.orodent.azdora.core.database.repository.GuestContactRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GuestContactRepositoryImpl implements GuestContactRepository {

    private final Connection conn;

    public GuestContactRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(GuestContact contact) {
        String sql = """
        INSERT INTO guest_contact (
            guest_id,
            contact_type,
            contact_value
        ) VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, contact.guestId());
            ps.setString(2, contact.contactType().name());
            ps.setString(3, contact.contactValue());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error inserting guest contact", e);
        }
    }
}
