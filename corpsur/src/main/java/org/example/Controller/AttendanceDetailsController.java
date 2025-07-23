package org.example.Controller;

import org.example.Config.DatabaseConnection;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDetailsController {

    // Obtener eventos con estado = 1 (abiertos)
    public List<Integer> getActiveEventIdsForTraining(int trainingId) throws SQLException {
        List<Integer> result = new ArrayList<>();
        String sql = """
            SELECT id FROM evento_asistencia
            WHERE capacitacion_id = ? AND estado = 1
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

    // Insertar detalle vacío al asignar un doctor
    public void insertAttendanceDetail(int eventId, int professionalId) throws SQLException {
        String sql = """
            INSERT INTO asistencia_detalle (evento_asistencia_id, profesional_id, estado)
            VALUES (?, ?, 1)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, professionalId);
            stmt.executeUpdate();
        }
    }

    // Obtener estado textual de asistencia
    public String getAttendanceStatus(int eventId, int professionalId) throws SQLException {
        String sql = """
            SELECT hora_entrada, hora_salida_almuerzo, hora_regreso_almuerzo, hora_salida_final, estado
            FROM asistencia_detalle
            WHERE evento_asistencia_id = ? AND profesional_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, professionalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (rs.getInt("estado") == 0) return "Cerrado";
                if (rs.getTime("hora_entrada") == null) return "No ha ingresado";
                if (rs.getTime("hora_salida_almuerzo") == null) return "Presente";
                if (rs.getTime("hora_regreso_almuerzo") == null) return "En almuerzo";
                if (rs.getTime("hora_salida_final") == null) return "Regresó";
                return "Finalizó";
            } else {
                return "Sin registro";
            }
        }
    }

    // Registrar una hora específica
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

    // Marcar como cerrada (estado = 0)
    public void cerrarAsistencia(int eventId, int professionalId) throws SQLException {
        String sql = """
            UPDATE asistencia_detalle SET estado = 0
            WHERE evento_asistencia_id = ? AND profesional_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, professionalId);
            stmt.executeUpdate();
        }
    }

    // Estado general del doctor para la capacitación actual
    public String getEstadoGeneralDelDoctor(int trainingId, String cedula) throws SQLException {
        String sql = """
        SELECT ad.hora_entrada, ad.hora_salida_almuerzo, ad.hora_regreso_almuerzo,
               ad.hora_salida_final, ad.estado
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
                if (rs.getInt("estado") == 0) return "Cerrado";
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
