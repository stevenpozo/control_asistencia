package org.example.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class StatusProfessionalView {

    public static void show(String nombreCompleto, boolean activar, Callback<Boolean, Void> callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 15;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);

        String mensajeTexto = activar
                ? "¿Habilitar al profesional " + nombreCompleto + "?"
                : "¿Inhabilitar al profesional " + nombreCompleto + "?";

        Label question = new Label(mensajeTexto);
        question.setFont(new Font("Segoe UI", 16));
        question.setTextFill(Color.WHITE);
        question.setWrapText(true);
        question.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button confirm = new Button(activar ? "Activar" : "Inhabilitar");
        confirm.setStyle(
                activar
                        ? "-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20;"
                        : "-fx-background-color: #f43f5e; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20;"
        );
        confirm.setOnAction(e -> {
            callback.call(true);
            dialog.close();
        });

        Button cancel = new Button("Cancelar");
        cancel.setStyle(
                "-fx-background-color: #374151; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20;"
        );
        cancel.setOnAction(e -> {
            callback.call(false);
            dialog.close();
        });

        buttons.getChildren().addAll(confirm, cancel);
        content.getChildren().addAll(question, buttons);

        root.getChildren().add(content);
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
