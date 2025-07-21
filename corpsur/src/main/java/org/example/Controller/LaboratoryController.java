package org.example.Controller;

import org.example.Config.DatabaseConnection;
import org.example.Model.LaboratoryModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LaboratoryController {

    // Get all Laboratories
    public List<LaboratoryModel> getAllLaboratories() throws SQLException {
        List<LaboratoryModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, nombre, activo FROM laboratorio ORDER BY LOWER(nombre)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            LaboratoryModel model = new LaboratoryModel(
                    rs.getString("nombre"),
                    rs.getInt("id")
            );
            model.setStatus(rs.getBoolean("activo"));
            list.add(model);
        }

        return list;
    }

    // Obtener solo laboratorios habilitados
    public List<LaboratoryModel> getActiveLaboratories() throws SQLException {
        List<LaboratoryModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, nombre, activo FROM laboratorio WHERE activo = 1 ORDER BY LOWER(nombre)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            LaboratoryModel model = new LaboratoryModel(rs.getString("nombre"), rs.getInt("id"));
            model.setStatus(true);
            list.add(model);
        }
        return list;
    }

    // Obtener solo laboratorios inhabilitados
    public List<LaboratoryModel> getInactiveLaboratories() throws SQLException {
        List<LaboratoryModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, nombre, activo FROM laboratorio WHERE activo = 0 ORDER BY LOWER(nombre)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            LaboratoryModel model = new LaboratoryModel(rs.getString("nombre"), rs.getInt("id"));
            model.setStatus(false);
            list.add(model);
        }
        return list;
    }



    //Insert laboratory
    public boolean insertLaboratory(String name) throws SQLException {
        if (laboratoryExists(name)) {
            throw new SQLException("El laboratorio '" + name + "' ya existe.");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO laboratorio (nombre, activo) VALUES (?,1)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        return stmt.executeUpdate() > 0;
    }

    //Verify existing Laboratory
    public boolean laboratoryExists(String name) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT COUNT(*) FROM laboratorio WHERE LOWER(nombre) = LOWER(?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    //UpdateLaboratory
    public boolean updateLaboratory(int id, String newName) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String checkSql = "SELECT COUNT(*) FROM laboratorio WHERE LOWER(nombre) = LOWER(?) AND id <> ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, newName);
        checkStmt.setInt(2, id);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            throw new SQLException("Ya existe otro laboratorio con el nombre '" + newName + "'.");
        }

        String updateSql = "UPDATE laboratorio SET nombre = ? WHERE id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
        updateStmt.setString(1, newName);
        updateStmt.setInt(2, id);
        return updateStmt.executeUpdate() > 0;
    }

    //Inactivate status laboratory if this is not relational with other entity
    public boolean inactivateLaboratoryStatus(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String checkSql = "SELECT COUNT(*) FROM profesional WHERE id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            throw new SQLException("No se puede inhabilitar: el laboratorio está asignado a uno o más capacitaciones.");
        }

        String deleteSql = "UPDATE laboratorio SET activo = 0 WHERE id = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
        deleteStmt.setInt(1, id);
        return deleteStmt.executeUpdate() > 0;
    }

    //Activate status laboratory
    public boolean activateLaboratoryStatus(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String deleteSql = "UPDATE laboratorio SET activo = 1 WHERE id = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
        deleteStmt.setInt(1, id);
        return deleteStmt.executeUpdate() > 0;
    }




}
