package com.orodent.azdora.core.database.implementation;

import com.orodent.azdora.core.database.model.Guest;
import com.orodent.azdora.core.database.repository.GuestRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public void updateNotes(long id, String notes) {

        String sql = """
        UPDATE guest
        SET notes = ?
        WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notes);
            ps.setLong(2, id);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("No guest found with id " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating guest notes id=" + id, e);
        }
    }

    @Override
    public void updateName(long id, String firstName, String lastName) {

        String sql = """
        UPDATE guest
        SET first_name = ?, last_name = ?
        WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setLong(3, id);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("No guest found with id " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating guest name id=" + id, e);
        }
    }

    @Override
    public void deleteById(long id) {

        String sql = """
        DELETE FROM guest
        WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                // puoi anche scegliere di NON lanciare eccezione e fare "no-op"
                throw new RuntimeException("No guest found with id " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting guest id=" + id, e);
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

    @Override
    public List<Guest> findAll() {

        List<Guest> guests = new ArrayList<>();

        String sql = """
        SELECT id, first_name, last_name, notes
        FROM guest
        ORDER BY last_name, first_name
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                guests.add(new Guest(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("notes")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading guests", e);
        }
        return guests;
    }
}
