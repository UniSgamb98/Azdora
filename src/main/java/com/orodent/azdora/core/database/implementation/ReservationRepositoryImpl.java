package com.orodent.azdora.core.database.implementation;

import com.orodent.azdora.core.database.model.Reservation;
import com.orodent.azdora.core.database.repository.ReservationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepositoryImpl implements ReservationRepository {

    private final Connection conn;

    public ReservationRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Reservation r) {
        String sql = """
        INSERT INTO reservation (
            ota_id,
            guest_id,
            provenance,
            check_in,
            check_out,
            nights,
            adult_guests_count,
            child_guests_count,
            total_amount,
            created_at,
            notes
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, r.otaId());
            ps.setLong(2, r.guestId());
            ps.setString(3, r.provenance());
            ps.setDate(4, java.sql.Date.valueOf(r.checkIn()));
            ps.setDate(5, java.sql.Date.valueOf(r.checkOut()));
            ps.setLong(6, r.nights());
            ps.setInt(7, r.adultGuestsCount());
            ps.setInt(8, r.childGuestsCount());
            ps.setBigDecimal(9, r.totalAmount());
            ps.setDate(10, java.sql.Date.valueOf(r.createdAt()));
            ps.setString(11, r.notes());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error inserting reservation", e);
        }
    }

    @Override
    public void updateProvenance(long reservationId, String provenance) {

        String sql = """
        UPDATE reservation
        SET provenance = ?
        WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, provenance);
            ps.setLong(2, reservationId);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error updating provenance for reservation id=" + reservationId, e);
        }
    }


    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservation ORDER BY check_in";

        List<Reservation> result = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Reservation(
                        rs.getLong("id"),
                        rs.getLong("ota_id"),
                        rs.getLong("guest_id"),
                        rs.getString("provenance"),
                        rs.getDate("check_in").toLocalDate(),
                        rs.getDate("check_out").toLocalDate(),
                        rs.getLong("nights"),
                        rs.getInt("adult_guests_count"),
                        rs.getInt("child_guests_count"),
                        rs.getBigDecimal("total_amount"),
                        rs.getDate("created_at").toLocalDate(),
                        rs.getString("notes")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading reservations", e);
        }

        return result;
    }
}
