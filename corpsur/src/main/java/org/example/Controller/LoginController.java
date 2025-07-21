package org.example.Controller;


import org.example.Config.DatabaseConnection;
import org.example.Config.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    public boolean authenticate(String username, String password) {
        String sql = "SELECT id, usuario FROM usuarios WHERE usuario = ? AND contraseña = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // guardamos el ID en sesión
                int userId = rs.getInt("id");
                Session.setCurrentUserId(userId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
