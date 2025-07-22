package org.example.Controller;

import org.example.Config.DatabaseConnection;
import org.example.Model.TrainingModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainingController {

    // Obtener todas las capacitaciones
    public List<TrainingModel> getAllTrainings() throws SQLException {
        List<TrainingModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = """
            SELECT c.id, c.nombre, c.codigo, c.fecha_inicio, c.fecha_fin, c.laboratorio_id, c.activo,
                   l.nombre AS laboratorio_nombre
            FROM capacitacion c
            JOIN laboratorio l ON c.laboratorio_id = l.id
            ORDER BY LOWER(c.nombre)
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TrainingModel model = new TrainingModel();
            model.setId(rs.getInt("id"));
            model.setNombre(rs.getString("nombre"));
            model.setCodigo(rs.getString("codigo"));
            model.setFechaInicio(rs.getString("fecha_inicio"));
            model.setFechaFin(rs.getString("fecha_fin"));
            model.setLaboratorioId(rs.getInt("laboratorio_id"));
            model.setLaboratorioNombre(rs.getString("laboratorio_nombre"));
            model.setActivo(rs.getBoolean("activo"));
            list.add(model);
        }
        return list;
    }

    // Obtener capacitaciones activas
    public List<TrainingModel> getActiveTrainings() throws SQLException {
        List<TrainingModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = """
            SELECT c.id, c.nombre, c.codigo, c.fecha_inicio, c.fecha_fin, c.laboratorio_id, c.activo,
                   l.nombre AS laboratorio_nombre
            FROM capacitacion c
            JOIN laboratorio l ON c.laboratorio_id = l.id
            WHERE c.activo = 1
            ORDER BY LOWER(c.nombre)
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TrainingModel model = new TrainingModel();
            model.setId(rs.getInt("id"));
            model.setNombre(rs.getString("nombre"));
            model.setCodigo(rs.getString("codigo"));
            model.setFechaInicio(rs.getString("fecha_inicio"));
            model.setFechaFin(rs.getString("fecha_fin"));
            model.setLaboratorioId(rs.getInt("laboratorio_id"));
            model.setLaboratorioNombre(rs.getString("laboratorio_nombre"));
            model.setActivo(true);
            list.add(model);
        }
        return list;
    }

    // Obtener capacitaciones inactivas
    public List<TrainingModel> getInactiveTrainings() throws SQLException {
        List<TrainingModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = """
            SELECT c.id, c.nombre, c.codigo, c.fecha_inicio, c.fecha_fin, c.laboratorio_id, c.activo,
                   l.nombre AS laboratorio_nombre
            FROM capacitacion c
            JOIN laboratorio l ON c.laboratorio_id = l.id
            WHERE c.activo = 0
            ORDER BY LOWER(c.nombre)
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TrainingModel model = new TrainingModel();
            model.setId(rs.getInt("id"));
            model.setNombre(rs.getString("nombre"));
            model.setCodigo(rs.getString("codigo"));
            model.setFechaInicio(rs.getString("fecha_inicio"));
            model.setFechaFin(rs.getString("fecha_fin"));
            model.setLaboratorioId(rs.getInt("laboratorio_id"));
            model.setLaboratorioNombre(rs.getString("laboratorio_nombre"));
            model.setActivo(false);
            list.add(model);
        }
        return list;
    }

    // Verificar si el código de capacitación ya existe
    public boolean codeExists(String codigo) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT COUNT(*) FROM capacitacion WHERE LOWER(codigo) = LOWER(?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, codigo);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    // Insertar capacitación
    public boolean insertTraining(TrainingModel training) throws SQLException {
        if (codeExists(training.getCodigo())) {
            throw new SQLException("Ya existe una capacitación con el código '" + training.getCodigo() + "'.");
        }
        Connection conn = DatabaseConnection.getConnection();
        String sql = """
            INSERT INTO capacitacion (nombre, codigo, fecha_inicio, fecha_fin, laboratorio_id, activo)
            VALUES (?, ?, ?, ?, ?, 1)
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, training.getNombre());
        stmt.setString(2, training.getCodigo());
        stmt.setString(3, training.getFechaInicio());
        stmt.setString(4, training.getFechaFin());
        stmt.setInt(5, training.getLaboratorioId());
        return stmt.executeUpdate() > 0;
    }

    // Actualizar capacitación
    public boolean updateTraining(int id, TrainingModel training) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String checkSql = "SELECT COUNT(*) FROM capacitacion WHERE LOWER(codigo) = LOWER(?) AND id <> ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, training.getCodigo());
        checkStmt.setInt(2, id);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            throw new SQLException("Ya existe otra capacitación con el código '" + training.getCodigo() + "'.");
        }

        String updateSql = """
            UPDATE capacitacion
            SET nombre = ?, codigo = ?, fecha_inicio = ?, fecha_fin = ?, laboratorio_id = ?
            WHERE id = ?
        """;
        PreparedStatement stmt = conn.prepareStatement(updateSql);
        stmt.setString(1, training.getNombre());
        stmt.setString(2, training.getCodigo());
        stmt.setString(3, training.getFechaInicio());
        stmt.setString(4, training.getFechaFin());
        stmt.setInt(5, training.getLaboratorioId());
        stmt.setInt(6, id);
        return stmt.executeUpdate() > 0;
    }

    // Inhabilitar capacitación
    public boolean inactivateTraining(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE capacitacion SET activo = 0 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        return stmt.executeUpdate() > 0;
    }

    // Activar capacitación
    public boolean activateTraining(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE capacitacion SET activo = 1 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        return stmt.executeUpdate() > 0;
    }
}
