package org.example.Controller;

import org.example.Config.DatabaseConnection;
import org.example.Model.AttendanceControlModel;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceControlController {

    private AttendanceControlModel mapResult(ResultSet rs) throws SQLException {
        return new AttendanceControlModel(
                rs.getInt("asistencia_detalle_id"),
                rs.getInt("evento_asistencia_id"),
                rs.getString("profesional_nombre"),
                rs.getString("capacitacion_nombre"),
                rs.getDate("fecha_evento").toLocalDate(),
                rs.getTime("hora_entrada") != null ? rs.getTime("hora_entrada").toLocalTime() : null,
                rs.getTime("hora_salida_almuerzo") != null ? rs.getTime("hora_salida_almuerzo").toLocalTime() : null,
                rs.getTime("hora_regreso_almuerzo") != null ? rs.getTime("hora_regreso_almuerzo").toLocalTime() : null,
                rs.getTime("hora_salida_final") != null ? rs.getTime("hora_salida_final").toLocalTime() : null,
                rs.getInt("estado") // Se guarda como int directamente
        );
    }

    private final String baseQuery = """
        SELECT ad.id AS asistencia_detalle_id, ea.id AS evento_asistencia_id,
               CONCAT(p.nombre, ' ', p.apellido) AS profesional_nombre,
               c.nombre AS capacitacion_nombre, ea.fecha AS fecha_evento,
               ad.hora_entrada, ad.hora_salida_almuerzo,
               ad.hora_regreso_almuerzo, ad.hora_salida_final,
               ad.estado
        FROM asistencia_detalle ad
        JOIN evento_asistencia ea ON ad.evento_asistencia_id = ea.id
        JOIN capacitacion c ON ea.capacitacion_id = c.id
        JOIN profesional p ON ad.profesional_id = p.id
    """;

    public List<AttendanceControlModel> getTodayOpenAttendances() throws SQLException {
        String sql = baseQuery + " WHERE ea.fecha = CURDATE() AND ad.estado = 1 ORDER BY p.apellido";
        return executeQuery(sql);
    }

    public List<AttendanceControlModel> getAllAttendances() throws SQLException {
        String sql = baseQuery + " ORDER BY ea.fecha DESC, p.apellido";
        return executeQuery(sql);
    }

    public List<AttendanceControlModel> getAttendancesByStatus(int estado) throws SQLException {
        String sql = baseQuery + " WHERE ad.estado = ? ORDER BY ea.fecha DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, estado);
            return executeQuery(stmt);
        }
    }

    public List<AttendanceControlModel> getAttendancesByDate(LocalDate fecha) throws SQLException {
        String sql = baseQuery + " WHERE ea.fecha = ? ORDER BY p.apellido";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fecha));
            return executeQuery(stmt);
        }
    }

    public List<AttendanceControlModel> getAttendancesByCapacitacion(String nombre) throws SQLException {
        String sql = baseQuery + " WHERE c.nombre = ? ORDER BY ea.fecha DESC, p.apellido";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            return executeQuery(stmt);
        }
    }

    private List<AttendanceControlModel> executeQuery(String sql) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<AttendanceControlModel> list = new ArrayList<>();
            while (rs.next()) list.add(mapResult(rs));
            return list;
        }
    }

    private List<AttendanceControlModel> executeQuery(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            List<AttendanceControlModel> list = new ArrayList<>();
            while (rs.next()) list.add(mapResult(rs));
            return list;
        }
    }

    public boolean updateHoraAsistencia(int asistenciaId, String campo, LocalTime hora) throws SQLException {
        String sql = "UPDATE asistencia_detalle SET " + campo + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, hora != null ? Time.valueOf(hora) : null);
            stmt.setInt(2, asistenciaId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateAsistenciaManual(int asistenciaId,
                                          LocalTime entrada,
                                          LocalTime salidaAlmuerzo,
                                          LocalTime regresoAlmuerzo,
                                          LocalTime salidaFinal) throws SQLException {
        String sql = """
            UPDATE asistencia_detalle SET hora_entrada = ?, hora_salida_almuerzo = ?,
                                          hora_regreso_almuerzo = ?, hora_salida_final = ?
            WHERE id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, entrada != null ? Time.valueOf(entrada) : null);
            stmt.setTime(2, salidaAlmuerzo != null ? Time.valueOf(salidaAlmuerzo) : null);
            stmt.setTime(3, regresoAlmuerzo != null ? Time.valueOf(regresoAlmuerzo) : null);
            stmt.setTime(4, salidaFinal != null ? Time.valueOf(salidaFinal) : null);
            stmt.setInt(5, asistenciaId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<String> getAllCapacitaciones() throws SQLException {
        String sql = "SELECT nombre FROM capacitacion WHERE activo = 1 ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<String> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }
            return lista;
        }
    }
}
