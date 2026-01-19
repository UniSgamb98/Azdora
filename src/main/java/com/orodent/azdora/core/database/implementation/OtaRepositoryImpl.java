package com.orodent.azdora.core.database.implementation;

import com.orodent.azdora.core.database.model.Ota;
import com.orodent.azdora.core.database.repository.OtaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OtaRepositoryImpl implements OtaRepository {

    private final Connection connection;

    public OtaRepositoryImpl(Connection conn) {
        this.connection = conn;
    }
    @Override
    public Ota findById(long id) {

        String sql = "SELECT id, name FROM ota WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

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
    public Ota insert(Ota ota) {
        String sql = "INSERT INTO ota(name) VALUES (?)";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ota.name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Ota(keys.getLong(1), ota.name());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore inserimento OTA", e);
        }

        throw new RuntimeException("Inserimento OTA fallito");
    }

    @Override
    public void insertAll(List<Ota> otas) {
        for (Ota o : otas) {
            insert(o);
        }
    }

    @Override
    public List<Ota> findAll() {
        String sql = "SELECT id, name FROM ota";

        List<Ota> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
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
