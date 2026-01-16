package com.orodent.azdora.core.database.implementation;

import com.orodent.azdora.core.database.model.Ota;
import com.orodent.azdora.core.database.repository.OtaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OtaRepositoryImpl implements OtaRepository {

    private final Connection conn;

    public OtaRepositoryImpl(Connection conn) {
        this.conn = conn;
    }
    @Override
    public Ota findById(long id) {

        String sql = "SELECT id, name FROM ota WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ota(
                            rs.getLong("id"),
                            rs.getString("name")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading OTA by id " + id, e);
        }

        return null; // oppure throw, dipende da come preferisci gestirlo
    }

    @Override
    public List<Ota> findAll() {
        String sql = "SELECT id, name FROM ota";

        List<Ota> result = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Ota(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading OTA", e);
        }

        return result;
    }
}
