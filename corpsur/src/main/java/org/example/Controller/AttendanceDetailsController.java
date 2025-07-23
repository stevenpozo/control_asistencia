package org.example.Controller;

import org.example.Config.DatabaseConnection;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDetailsController {

    // Verificar si hay eventos activos para una capacitación (cerrado = 0)
    public List<Integer> getActiveEventIdsForTraining(int trainingId) throws SQLException {
        List<Integer> result = new ArrayList<>();
        String sql = """
            SELECT id FROM evento_asistencia
            WHERE capacitacion_id = ? AND cerrado = 0
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(rs.getInt("id"));
            }
        }

        return result;
    }

    // Registrar un nuevo detalle de asistencia (cuando se asigna un doctor a evento)
    public void insertAttendanceDetail(int eventId, int professionalId) throws SQLException {
        String sql = """
            INSERT INTO asistencia_detalle (evento_asistencia_id, profesional_id)
            VALUES (?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, professionalId);
            stmt.executeUpdate();
        }
    }

    // Obtener el estado de asistencia de un profesional para un evento
    public String getAttendanceStatus(int eventId, int professionalId) throws SQLException {
        String sql = """
            SELECT hora_entrada, hora_salida_almuerzo, hora_regreso_almuerzo, hora_salida_final, cerrado
            FROM asistencia_detalle
            WHERE evento_asistencia_id = ? AND profesional_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, professionalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (!rs.getBoolean("cerrado")) {
                    if (rs.getTime("hora_entrada") == null) return "No ha ingresado";
                    if (rs.getTime("hora_salida_almuerzo") == null) return "Presente";
                    if (rs.getTime("hora_regreso_almuerzo") == null) return "En almuerzo";
                    if (rs.getTime("hora_salida_final") == null) return "Regresó";
                    return "Finalizó";
                } else {
                    return "Cerrado";
                }
            } else {
                return "Sin registro";
            }
        }
    }

    // Método auxiliar para registrar una hora específica
    public void registrarHora(int eventId, int professionalId, String campo, LocalTime hora) throws SQLException {
        String sql = "UPDATE asistencia_detalle SET " + campo + " = ? WHERE evento_asistencia_id = ? AND profesional_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTime(1, Time.valueOf(hora));
            stmt.setInt(2, eventId);
            stmt.setInt(3, professionalId);
            stmt.executeUpdate();
        }
    }

    // Método para cerrar el detalle de asistencia de un profesional
    public void cerrarAsistencia(int eventId, int professionalId) throws SQLException {
        String sql = """
            UPDATE asistencia_detalle SET cerrado = 1
            WHERE evento_asistencia_id = ? AND profesional_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, professionalId);
            stmt.executeUpdate();
        }
    }

    public String getEstadoGeneralDelDoctor(int trainingId, String cedula) throws SQLException {
        String sql = """
        SELECT ad.hora_entrada, ad.hora_salida_almuerzo, ad.hora_regreso_almuerzo,
               ad.hora_salida_final, ad.cerrado
        FROM asistencia_detalle ad
        JOIN evento_asistencia ea ON ad.evento_asistencia_id = ea.id
        JOIN profesional p ON p.id = ad.profesional_id
        WHERE ea.capacitacion_id = ?
          AND ea.fecha = CURRENT_DATE
          AND p.cedula = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainingId);
            stmt.setString(2, cedula);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (rs.getBoolean("cerrado")) return "Cerrado";
                if (rs.getTime("hora_entrada") == null) return "No ha ingresado";
                if (rs.getTime("hora_salida_almuerzo") == null) return "Presente";
                if (rs.getTime("hora_regreso_almuerzo") == null) return "En almuerzo";
                if (rs.getTime("hora_salida_final") == null) return "Regresó";
                return "Finalizó";
            }
        }

        return "Sin registro";
    }

}
