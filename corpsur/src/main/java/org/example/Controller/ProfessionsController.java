package org.example.Controller;

import org.example.Config.DatabaseConnection;
import org.example.Model.ProfessionsModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessionsController {

    public List<ProfessionsModel> getActiveProfessions() throws SQLException {
        return getProfessionsByStatus(true);
    }

    public List<ProfessionsModel> getInactiveProfessions() throws SQLException {
        return getProfessionsByStatus(false);
    }

    private List<ProfessionsModel> getProfessionsByStatus(boolean active) throws SQLException {
        List<ProfessionsModel> list = new ArrayList<>();
        String sql = "SELECT id, nombre, activo FROM profesiones WHERE activo = ? ORDER BY LOWER(nombre)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, active ? 1 : 0);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProfessionsModel p = new ProfessionsModel(rs.getString("nombre"), rs.getInt("id"));
                p.setStatus(rs.getBoolean("activo"));
                list.add(p);
            }
        }
        return list;
    }

    public boolean professionExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM profesiones WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean insertProfession(String name) throws SQLException {
        if (professionExists(name)) {
            throw new SQLException("La profesión '" + name + "' ya existe.");
        }
        String sql = "INSERT INTO profesiones (nombre, activo) VALUES (?, 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateProfession(int id, String newName) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM profesiones WHERE LOWER(nombre) = LOWER(?) AND id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, newName);
            checkStmt.setInt(2, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Ya existe otra profesión con el nombre '" + newName + "'.");
            }

            String updateSql = "UPDATE profesiones SET nombre = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newName);
            updateStmt.setInt(2, id);
            return updateStmt.executeUpdate() > 0;
        }
    }

    public boolean activateProfession(int id) throws SQLException {
        String sql = "UPDATE profesiones SET activo = 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean inactivateProfession(int id) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM profesional WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("No se puede inhabilitar: la profesión está asignada.");
            }
        }

        String sql = "UPDATE profesiones SET activo = 0 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
