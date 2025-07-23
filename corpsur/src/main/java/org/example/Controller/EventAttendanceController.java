package org.example.Controller;

import org.example.Config.DatabaseConnection;
import org.example.Model.EventAttendanceModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventAttendanceController {

    // Obtener capacitaciones activas con nombre y fechas
    public List<String[]> getActiveCapacitationsWithDateRange() throws SQLException {
        List<String[]> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        String sql = "SELECT id, codigo, nombre, fecha_inicio, fecha_fin FROM capacitacion WHERE activo = 1 ORDER BY nombre";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            list.add(new String[]{
                    rs.getString("id"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin")
            });
        }
        return list;
    }

    // Verificar si ya existe un día registrado
    public boolean dayExists(int capacitacionId, String fecha) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT COUNT(*) FROM evento_asistencia WHERE capacitacion_id = ? AND fecha = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, capacitacionId);
        stmt.setString(2, fecha);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    // Crear día de asistencia con estado ACTIVO (estado = 1)
    public boolean createDay(int capacitacionId, String fecha) throws SQLException {
        if (dayExists(capacitacionId, fecha)) {
            throw new SQLException("Ya existe una asistencia registrada para esa fecha.");
        }
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO evento_asistencia (capacitacion_id, fecha, estado) VALUES (?, ?, 1)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, capacitacionId);
        stmt.setString(2, fecha);
        return stmt.executeUpdate() > 0;
    }

    // Obtener asistencias de una fecha específica
    public List<EventAttendanceModel> getEventAttendancesByDate(String fecha) throws SQLException {
        List<EventAttendanceModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        String sql = """
            SELECT ea.id, ea.capacitacion_id, ea.fecha, ea.estado,
                   c.codigo, c.nombre
            FROM evento_asistencia ea
            JOIN capacitacion c ON ea.capacitacion_id = c.id
            WHERE ea.fecha = ?
            ORDER BY c.nombre
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, fecha);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            EventAttendanceModel model = new EventAttendanceModel();
            model.setId(rs.getInt("id"));
            model.setCapacitacionId(rs.getInt("capacitacion_id"));
            model.setFecha(rs.getString("fecha"));
            model.setEstado(rs.getInt("estado"));
            model.setCodigoCapacitacion(rs.getString("codigo"));
            model.setNombreCapacitacion(rs.getString("nombre"));
            list.add(model);
        }

        return list;
    }

    // Inactivar evento (cerrar) → estado = 0
    public boolean closeEvent(int eventId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String sql1 = "UPDATE evento_asistencia SET estado = 0 WHERE id = ?";
        String sql2 = "UPDATE asistencia_detalle SET estado = 0 WHERE evento_asistencia_id = ?";

        try (PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt1.setInt(1, eventId);
            stmt2.setInt(1, eventId);
            stmt1.executeUpdate();
            stmt2.executeUpdate();
            return true;
        }
    }

    // Activar evento (abrir) → estado = 1
    public boolean openEvent(int eventId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String sql1 = "UPDATE evento_asistencia SET estado = 1 WHERE id = ?";
        String sql2 = "UPDATE asistencia_detalle SET estado = 1 WHERE evento_asistencia_id = ?";

        try (PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt1.setInt(1, eventId);
            stmt2.setInt(1, eventId);
            stmt1.executeUpdate();
            stmt2.executeUpdate();
            return true;
        }
    }

    // Cerrar automáticamente eventos pasados → estado = 0
    public void autoClosePastEvents() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String sqlEventos = """
            UPDATE evento_asistencia
            SET estado = 0
            WHERE fecha < CURDATE() AND estado = 1
        """;

        String sqlDetalles = """
            UPDATE asistencia_detalle
            SET estado = 0
            WHERE evento_asistencia_id IN (
                SELECT id FROM evento_asistencia
                WHERE fecha < CURDATE() AND estado = 1
            )
        """;

        try (PreparedStatement stmt1 = conn.prepareStatement(sqlEventos);
             PreparedStatement stmt2 = conn.prepareStatement(sqlDetalles)) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
        }
    }

    // Obtener todos los eventos ordenados por fecha
    public List<EventAttendanceModel> getAllEventAttendancesOrderedByDate() throws SQLException {
        List<EventAttendanceModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        String sql = """
        SELECT ea.id, ea.capacitacion_id, ea.fecha, ea.estado,
               c.codigo, c.nombre
        FROM evento_asistencia ea
        JOIN capacitacion c ON ea.capacitacion_id = c.id
        ORDER BY ea.fecha DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            EventAttendanceModel model = new EventAttendanceModel();
            model.setId(rs.getInt("id"));
            model.setCapacitacionId(rs.getInt("capacitacion_id"));
            model.setFecha(rs.getString("fecha"));
            model.setEstado(rs.getInt("estado"));
            model.setCodigoCapacitacion(rs.getString("codigo"));
            model.setNombreCapacitacion(rs.getString("nombre"));
            list.add(model);
        }

        return list;
    }

    // Obtener lista de capacitaciones activas para el filtro
    public List<String[]> getCapacitationFilterList() throws SQLException {
        List<String[]> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, nombre FROM capacitacion WHERE activo = 1 ORDER BY LOWER(nombre)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre")
            });
        }
        return list;
    }

    // Obtener eventos por capacitación
    public List<EventAttendanceModel> getEventAttendancesByCapId(int capId) throws SQLException {
        List<EventAttendanceModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        String sql = """
        SELECT ea.id, ea.capacitacion_id, ea.fecha, ea.estado,
               c.nombre AS nombre_capacitacion, c.codigo AS codigo_capacitacion
        FROM evento_asistencia ea
        JOIN capacitacion c ON ea.capacitacion_id = c.id
        WHERE ea.capacitacion_id = ?
        ORDER BY ea.fecha DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, capId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            EventAttendanceModel model = new EventAttendanceModel();
            model.setId(rs.getInt("id"));
            model.setCapacitacionId(rs.getInt("capacitacion_id"));
            model.setFecha(rs.getString("fecha"));
            model.setEstado(rs.getInt("estado"));
            model.setNombreCapacitacion(rs.getString("nombre_capacitacion"));
            model.setCodigoCapacitacion(rs.getString("codigo_capacitacion"));
            list.add(model);
        }

        return list;
    }
}
