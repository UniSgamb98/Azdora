package com.orodent.azdora.core.database.implementation;

import com.orodent.azdora.core.database.model.ContactType;
import com.orodent.azdora.core.database.model.GuestContact;
import com.orodent.azdora.core.database.repository.GuestContactRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestContactRepositoryImpl implements GuestContactRepository {

    private final Connection conn;

    public GuestContactRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public GuestContact insert(GuestContact contact) {
        String sql = """
        INSERT INTO guest_contact (
            guest_id,
            contact_type,
            contact_value
        ) VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, contact.guestId());
            ps.setString(2, contact.contactType().name());
            ps.setString(3, contact.contactValue());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new GuestContact(
                            id,
                            contact.guestId(),
                            contact.contactType(),
                            contact.contactValue()
                    );
                }
            }

            throw new RuntimeException("Insert guest_contact failed: no generated key");

        } catch (Exception e) {
            throw new RuntimeException("Error inserting guest contact", e);
        }
    }

    @Override
    public List<GuestContact> findByGuestId(long guestId) {
        String sql = """
        SELECT id, guest_id, contact_type, contact_value
        FROM guest_contact
        WHERE guest_id = ?
        ORDER BY id
        """;

        List<GuestContact> out = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, guestId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new GuestContact(
                            rs.getLong("id"),
                            rs.getLong("guest_id"),
                            ContactType.valueOf(rs.getString("contact_type")),
                            rs.getString("contact_value")
                    ));
                }
            }
            return out;

        } catch (Exception e) {
            throw new RuntimeException("Error reading guest contacts for guest_id=" + guestId, e);
        }
    }

    @Override
    public void updateType(long contactId, ContactType type) {
        String sql = """
        UPDATE guest_contact
        SET contact_type = ?
        WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            ps.setLong(2, contactId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("No guest_contact found with id " + contactId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error updating guest_contact type id=" + contactId, e);
        }
    }

    @Override
    public void updateValue(long contactId, String value) {
        String sql = """
        UPDATE guest_contact
        SET contact_value = ?
        WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setLong(2, contactId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("No guest_contact found with id " + contactId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error updating guest_contact value id=" + contactId, e);
        }
    }

    @Override
    public void deleteById(long contactId) {
        String sql = """
        DELETE FROM guest_contact
        WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, contactId);

            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new RuntimeException("No guest_contact found with id " + contactId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error deleting guest_contact id=" + contactId, e);
        }
    }
}
