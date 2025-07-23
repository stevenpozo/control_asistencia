package org.example.Controller;

import org.example.Config.DatabaseConnection;
import org.example.Model.ProfessionalTrainingModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessionalTrainingController {

    // Obtener todas las capacitaciones activas con su cantidad de doctores asignados
    public List<String[]> getAllActiveTrainingsWithCount() throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = """
            SELECT c.id, c.codigo, c.nombre, c.fecha_inicio, c.fecha_fin, l.nombre AS laboratorio,
                   COUNT(pc.id) AS total_doctores
            FROM capacitacion c
            LEFT JOIN laboratorio l ON c.laboratorio_id = l.id
            LEFT JOIN profesional_capacitacion pc ON pc.capacitacion_id = c.id
            WHERE c.activo = 1
            GROUP BY c.id
            ORDER BY c.fecha_inicio DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(new String[]{
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin"),
                        rs.getString("laboratorio"),
                        rs.getString("total_doctores")
                });
            }
        }

        return result;
    }

    // Buscar capacitación por código
    public String[] findTrainingByCode(String code) throws SQLException {
        String sql = """
            SELECT id, nombre FROM capacitacion WHERE codigo = ? AND activo = 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new String[]{rs.getString("id"), rs.getString("nombre")};
            } else {
                return null;
            }
        }
    }

    // Verificar si un profesional ya está inscrito en una capacitación
    public boolean existsProfessionalInTraining(int professionalId, int trainingId) throws SQLException {
        String sql = """
            SELECT 1 FROM profesional_capacitacion
            WHERE profesional_id = ? AND capacitacion_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, professionalId);
            stmt.setInt(2, trainingId);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        }
    }

    // Inscribir un profesional a una capacitación
    public void assignProfessionalToTraining(ProfessionalTrainingModel model) throws SQLException {
        String sql = """
            INSERT INTO profesional_capacitacion (profesional_id, capacitacion_id)
            VALUES (?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, model.getProfessionalId());
            stmt.setInt(2, model.getTrainingId());
            stmt.executeUpdate();
        }
    }

    // Obtener lista de profesionales inscritos a una capacitación
    public List<String[]> getProfessionalsByTraining(int trainingId) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = """
            SELECT p.cedula, p.nombre, p.apellido, p.fecha_nacimiento, p.telefono, p.activo
            FROM profesional p
            JOIN profesional_capacitacion pc ON pc.profesional_id = p.id
            WHERE pc.capacitacion_id = ?
            ORDER BY p.apellido, p.nombre
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new String[]{
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("fecha_nacimiento"),
                        rs.getString("telefono"),
                        rs.getBoolean("activo") ? "Activo" : "Inactivo"
                });
            }
        }

        return result;
    }
}
