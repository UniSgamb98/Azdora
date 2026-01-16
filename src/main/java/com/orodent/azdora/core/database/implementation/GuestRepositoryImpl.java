package com.orodent.azdora.core.database.implementation;

import com.orodent.azdora.core.database.model.Guest;
import com.orodent.azdora.core.database.repository.GuestRepository;

import java.sql.*;

public class GuestRepositoryImpl implements GuestRepository {

    private final Connection conn;

    public GuestRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Guest insert(Guest guest) {
        String sql = """
        INSERT INTO guest (
            first_name,
            last_name,
            notes
        ) VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, guest.firstName());
            ps.setString(2, guest.lastName());
            ps.setString(3, guest.notes());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Guest(
                            rs.getLong(1),
                            guest.firstName(),
                            guest.lastName(),
                            guest.notes()
                    );
                }
            }

            throw new RuntimeException("Guest ID not generated");

        } catch (Exception e) {
            throw new RuntimeException("Error inserting guest", e);
        }
    }

    @Override
    public Guest findById(long id) {

        String sql = """
    SELECT id, first_name, last_name, notes
    FROM guest
    WHERE id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Guest(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("notes")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading guest by id " + id, e);
        }

        return null; // coerente col resto del progetto
    }

}
