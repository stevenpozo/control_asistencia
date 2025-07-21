package org.example.Controller;

import org.example.Config.DatabaseConnection;
import org.example.Model.ProfessionalModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessionalController {

    // Mantener: Obtener todas las provincias
    public ArrayList<String[]> getAllProvinces() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nombre FROM provincia");
             ResultSet rs = stmt.executeQuery()) {

            ArrayList<String[]> provinces = new ArrayList<>();
            while (rs.next()) {
                provinces.add(new String[]{rs.getString("id"), rs.getString("nombre")});
            }
            return provinces;
        }
    }

    // Mantener: Obtener ciudades por provincia
    public ArrayList<String[]> getCitiesByProvince(int provinceId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nombre FROM ciudad WHERE provincia_id = ?")) {

            stmt.setInt(1, provinceId);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String[]> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(new String[]{rs.getString("id"), rs.getString("nombre")});
            }
            return cities;
        }
    }

    public ArrayList<String[]> getAllProfessions() throws SQLException {
        ArrayList<String[]> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, nombre FROM profesiones WHERE activo = 1 ORDER BY LOWER(nombre)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(new String[]{rs.getString("id"), rs.getString("nombre")});
        }
        return list;
    }


    // Mantener: Verificar existencia por cédula
    public boolean existsByDni(String dni) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM profesional WHERE cedula = ?")) {

            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    // Obtener todos los profesionales
    public List<ProfessionalModel> getAllProfessionals() throws SQLException {
        List<ProfessionalModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, cedula, nombre, apellido, fecha_nacimiento, telefono, provincia_id, ciudad_id, profesion_id, activo FROM profesional ORDER BY LOWER(apellido)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ProfessionalModel model = new ProfessionalModel();
            model.setId(rs.getInt("id"));
            model.setCedula(rs.getString("cedula"));
            model.setNombre(rs.getString("nombre"));
            model.setApellido(rs.getString("apellido"));
            model.setFechaNacimiento(rs.getString("fecha_nacimiento"));
            model.setTelefono(rs.getString("telefono"));
            model.setProvinciaId(rs.getInt("provincia_id"));
            model.setCiudadId(rs.getInt("ciudad_id"));
            model.setProfesionId(rs.getInt("profesion_id"));
            model.setActivo(rs.getBoolean("activo"));
            list.add(model);
        }

        return list;
    }

    // Obtener solo profesionales habilitados
    public List<ProfessionalModel> getActiveProfessionals() throws SQLException {
        List<ProfessionalModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = """
        SELECT 
            p.id,
            p.nombre,
            p.apellido,
            p.cedula,
            p.telefono,
            p.fecha_nacimiento,
            p.activo,
            provincia.nombre AS provincia_nombre,
            ciudad.nombre AS ciudad_nombre,
            prof.nombre AS profesion_nombre
        FROM profesional p
        JOIN provincia ON p.provincia_id = provincia.id
        JOIN ciudad ON p.ciudad_id = ciudad.id
        JOIN profesiones prof ON p.profesion_id = prof.id
        WHERE p.activo = 1
        ORDER BY p.apellido
    """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ProfessionalModel p = new ProfessionalModel();
            p.setId(rs.getInt("id")); // corregido
            p.setNombre(rs.getString("nombre"));
            p.setApellido(rs.getString("apellido"));
            p.setCedula(rs.getString("cedula"));
            p.setTelefono(rs.getString("telefono")); // corregido
            p.setFechaNacimiento(rs.getString("fecha_nacimiento")); // corregido
            p.setActivo(rs.getBoolean("activo"));

            p.setProvinciaNombre(rs.getString("provincia_nombre"));
            p.setCiudadNombre(rs.getString("ciudad_nombre"));
            p.setProfesionNombre(rs.getString("profesion_nombre"));

            list.add(p);
        }

        return list;
    }

    // Obtener solo profesionales inhabilitados
    public List<ProfessionalModel> getInactiveProfessionals() throws SQLException {
        List<ProfessionalModel> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        String sql = """
        SELECT 
            p.id,
            p.nombre,
            p.apellido,
            p.cedula,
            p.telefono,
            p.fecha_nacimiento,
            p.activo,
            provincia.nombre AS provincia_nombre,
            ciudad.nombre AS ciudad_nombre,
            prof.nombre AS profesion_nombre
        FROM profesional p
        JOIN provincia ON p.provincia_id = provincia.id
        JOIN ciudad ON p.ciudad_id = ciudad.id
        JOIN profesiones prof ON p.profesion_id = prof.id
        WHERE p.activo = 0
        ORDER BY p.apellido
    """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            ProfessionalModel p = new ProfessionalModel();
            p.setId(rs.getInt("id")); // corregido
            p.setNombre(rs.getString("nombre"));
            p.setApellido(rs.getString("apellido"));
            p.setCedula(rs.getString("cedula"));
            p.setTelefono(rs.getString("telefono")); // corregido
            p.setFechaNacimiento(rs.getString("fecha_nacimiento")); // corregido
            p.setActivo(rs.getBoolean("activo"));

            p.setProvinciaNombre(rs.getString("provincia_nombre"));
            p.setCiudadNombre(rs.getString("ciudad_nombre"));
            p.setProfesionNombre(rs.getString("profesion_nombre"));

            list.add(p);
        }

        return list;
    }



    // Insertar profesional
    public boolean insertProfessional(ProfessionalModel p) throws SQLException {
        if (existsByDni(p.getCedula())) {
            throw new SQLException("El profesional con cédula '" + p.getCedula() + "' ya existe.");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO profesional (cedula, nombre, apellido, fecha_nacimiento, telefono, provincia_id, ciudad_id, profesion_id, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, p.getCedula());
        stmt.setString(2, p.getNombre());
        stmt.setString(3, p.getApellido());
        stmt.setString(4, p.getFechaNacimiento());
        stmt.setString(5, p.getTelefono());
        stmt.setInt(6, p.getProvinciaId());
        stmt.setInt(7, p.getCiudadId());
        stmt.setInt(8, p.getProfesionId());
        return stmt.executeUpdate() > 0;
    }

    // Actualizar profesional
    public boolean updateProfessional(int id, ProfessionalModel p) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String checkSql = "SELECT COUNT(*) FROM profesional WHERE LOWER(cedula) = LOWER(?) AND id <> ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, p.getCedula());
        checkStmt.setInt(2, id);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            throw new SQLException("Ya existe otro profesional con la cédula '" + p.getCedula() + "'.");
        }

        String updateSql = "UPDATE profesional SET cedula = ?, nombre = ?, apellido = ?, fecha_nacimiento = ?, telefono = ?, provincia_id = ?, ciudad_id = ?, profesion_id = ? WHERE id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
        updateStmt.setString(1, p.getCedula());
        updateStmt.setString(2, p.getNombre());
        updateStmt.setString(3, p.getApellido());
        updateStmt.setString(4, p.getFechaNacimiento());
        updateStmt.setString(5, p.getTelefono());
        updateStmt.setInt(6, p.getProvinciaId());
        updateStmt.setInt(7, p.getCiudadId());
        updateStmt.setInt(8, p.getProfesionId());
        updateStmt.setInt(9, id);
        return updateStmt.executeUpdate() > 0;
    }

    // Inhabilitar profesional
    public boolean inactivateProfessionalStatus(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE profesional SET activo = 0 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        return stmt.executeUpdate() > 0;
    }

    // Activar profesional
    public boolean activateProfessionalStatus(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE profesional SET activo = 1 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        return stmt.executeUpdate() > 0;
    }
}
