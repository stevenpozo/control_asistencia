// AsistenciasController.java
package org.example.Controller;

import org.example.Config.DatabaseConnection;
import org.example.Model.AsistenciasModel;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AsistenciasController {

    /**
     * Obtiene todos los eventos de asistencia para una fecha dada.
     */
    public List<AsistenciasModel> getByDate(LocalDate fecha) throws SQLException {
        List<AsistenciasModel> list = new ArrayList<>();
        String sql = "SELECT id, fecha, tipo, estado FROM corpsur.asistencias WHERE fecha = ? ORDER BY FIELD(tipo, 'INGRESO','SALIDA_ALMUERZO','INGRESO_ALMUERZO','SALIDA_FINAL')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new AsistenciasModel(
                            rs.getInt("id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("tipo"),
                            rs.getInt("estado") == 1
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Genera los eventos estándar para una fecha:
     * INGRESO, SALIDA_ALMUERZO, INGRESO_ALMUERZO, SALIDA_FINAL.
     * Lanza excepción si ya existen filas para esa fecha y tipo.
     */
    public boolean generarEventosParaFecha(LocalDate fecha) throws SQLException {
        // Verificar que no existan ya esos cuatro tipos
        List<AsistenciasModel> existentes = getByDate(fecha);
        if (!existentes.isEmpty()) {
            throw new SQLException("Ya existen eventos de asistencia para la fecha " + fecha);
        }

        String sql = "INSERT INTO corpsur.asistencias(fecha, tipo, estado) VALUES " +
                "(?, 'INGRESO', 1), " +
                "(?, 'SALIDA_ALMUERZO', 1), " +
                "(?, 'INGRESO_ALMUERZO', 1), " +
                "(?, 'SALIDA_FINAL', 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Date sqlDate = Date.valueOf(fecha);
            stmt.setDate(1, sqlDate);
            stmt.setDate(2, sqlDate);
            stmt.setDate(3, sqlDate);
            stmt.setDate(4, sqlDate);
            return stmt.executeUpdate() == 4;
        }
    }

    /**
     * Cierra un evento de asistencia (estado = 0).
     */
    public boolean cerrarEvento(int id) throws SQLException {
        String sql = "UPDATE corpsur.asistencias SET estado = 0 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Reabre un evento de asistencia (estado = 1).
     */
    public boolean reabrirEvento(int id) throws SQLException {
        String sql = "UPDATE corpsur.asistencias SET estado = 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
