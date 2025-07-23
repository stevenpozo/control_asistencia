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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ConfirmAttendanceControlView {

    public static void show(String accion, String nombreDoctor, LocalTime hora, Callback<Boolean, Void> callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Confirmar Asistencia");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);

        // Título general
        Label titleLabel = new Label("¿Desea registrar esta asistencia?");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);

        // Acción: Entrada de Juan Pérez
        Label actionLabel = new Label(accion + " de " + nombreDoctor);
        actionLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13px;");
        actionLabel.setAlignment(Pos.CENTER);

        // Hora actual
        Label horaLabel = new Label("Hora: " + hora.format(DateTimeFormatter.ofPattern("HH:mm")));
        horaLabel.setStyle("-fx-text-fill: #eab308; -fx-font-size: 13px;");
        horaLabel.setAlignment(Pos.CENTER);

        // Botones
        Button btnConfirmar = new Button("Confirmar");
        btnConfirmar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #374151; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;");

        HBox buttonRow = new HBox(15, btnConfirmar, btnCancelar);
        buttonRow.setAlignment(Pos.CENTER);

        btnConfirmar.setOnAction(e -> {
            callback.call(true);
            dialog.close();
        });

        btnCancelar.setOnAction(e -> {
            callback.call(false);
            dialog.close();
        });

        content.getChildren().addAll(titleLabel, actionLabel, horaLabel, buttonRow);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 400, 250);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
