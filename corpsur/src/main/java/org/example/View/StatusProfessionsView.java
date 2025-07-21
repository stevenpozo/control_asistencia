package org.example.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class StatusProfessionsView {

    public static void show(String nombreProfesion, boolean activar, Callback<Boolean, Void> callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(activar ? "Confirmar Activación" : "Confirmar Inactivación");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        String mensajeTexto = activar
                ? "¿Estás seguro de que deseas *activar* la profesión\n'" + nombreProfesion + "'?"
                : "¿Estás seguro de que deseas *inhabilitar* la profesión\n'" + nombreProfesion + "'?";

        Label mensaje = new Label(mensajeTexto);
        mensaje.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        mensaje.setWrapText(true);
        mensaje.setAlignment(Pos.CENTER);

        Button btnAceptar = new Button(activar ? "Activar" : "Inhabilitar");
        btnAceptar.setStyle(
                activar
                        ? "-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;"
                        : "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;"
        );

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #374151; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;");

        HBox buttonRow = new HBox(15, btnAceptar, btnCancelar);
        buttonRow.setAlignment(Pos.CENTER);

        btnAceptar.setOnAction(e -> {
            callback.call(true);
            dialog.close();
        });

        btnCancelar.setOnAction(e -> {
            callback.call(false);
            dialog.close();
        });

        content.getChildren().addAll(mensaje, buttonRow);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 420, 180);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
