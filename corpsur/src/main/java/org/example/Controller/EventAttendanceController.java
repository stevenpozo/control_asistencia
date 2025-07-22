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

    // Crear día de asistencia
    public boolean createDay(int capacitacionId, String fecha) throws SQLException {
        if (dayExists(capacitacionId, fecha)) {
            throw new SQLException("Ya existe una asistencia registrada para esa fecha.");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO evento_asistencia (capacitacion_id, fecha, cerrado) VALUES (?, ?, 1)";
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
            SELECT ea.id, ea.capacitacion_id, ea.fecha, ea.cerrado,
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
            model.setCerrado(rs.getBoolean("cerrado"));
            model.setCodigoCapacitacion(rs.getString("codigo"));
            model.setNombreCapacitacion(rs.getString("nombre"));
            list.add(model);
        }

        return list;
    }

    // Cerrar un evento de asistencia
    public boolean closeEvent(int eventId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE evento_asistencia SET cerrado = 1 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, eventId);
        return stmt.executeUpdate() > 0;
    }

    // Abrir un evento de asistencia
    public boolean openEvent(int eventId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE evento_asistencia SET cerrado = 0 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, eventId);
        return stmt.executeUpdate() > 0;
    }

    // Cerrar automáticamente eventos que ya pasaron
    public void autoClosePastEvents() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = """
            UPDATE evento_asistencia
            SET cerrado = 1
            WHERE fecha < CURDATE() AND cerrado = 0
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
    }


    public List<EventAttendanceModel> getAllEventAttendancesOrderedByDate() throws SQLException {
        List<EventAttendanceModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        String sql = """
        SELECT ea.id, ea.capacitacion_id, ea.fecha, ea.cerrado,
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
            model.setCerrado(rs.getBoolean("cerrado"));
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

    // Obtener eventos de asistencia por ID de capacitación
    public List<EventAttendanceModel> getEventAttendancesByCapId(int capId) throws SQLException {
        List<EventAttendanceModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = """
        SELECT ea.id, ea.capacitacion_id, ea.fecha, ea.cerrado,
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
            model.setCerrado(rs.getBoolean("cerrado"));
            model.setNombreCapacitacion(rs.getString("nombre_capacitacion"));
            model.setCodigoCapacitacion(rs.getString("codigo_capacitacion"));
            list.add(model);
        }

        return list;
    }



}
