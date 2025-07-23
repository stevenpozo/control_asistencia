package org.example.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;

public class ConfirmProfessionalTrainingView {

    public static void show(String nombreCurso, List<String> nombresDoctores, Callback<Boolean, Void> callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Confirmar Inscripción");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("¿Desea inscribir a los siguientes doctores?");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);

        Label courseLabel = new Label("Curso: " + nombreCurso);
        courseLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13px;");
        courseLabel.setAlignment(Pos.CENTER);

        // Lista de nombres
        ListView<String> listView = new ListView<>();
        listView.setItems(javafx.collections.FXCollections.observableArrayList(nombresDoctores));
        listView.setPrefHeight(Math.min(200, nombresDoctores.size() * 28 + 10));

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

        content.getChildren().addAll(titleLabel, courseLabel, listView, buttonRow);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 500, 350);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
