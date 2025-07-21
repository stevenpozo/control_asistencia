package org.example.View;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.Controller.ProfessionsController;
import org.example.Utils.ToastUtil;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.function.Consumer;

public class AddProfessionView {

    public static void show(Consumer<String> callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nueva Profesión");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        InputStream iconStream = AddProfessionView.class.getResourceAsStream("/img/profession-icon.png");
        if (iconStream != null) {
            ImageView icon = new ImageView(new Image(iconStream));
            icon.setFitWidth(50);
            icon.setFitHeight(50);
            content.getChildren().add(icon);
        }

        Label label = new Label("Nombre de la Profesión");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField textField = new TextField();
        textField.setPromptText("Ej: Pediatra");
        textField.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        textField.setMaxWidth(280);

        Button btnSave = new Button("Guardar");
        btnSave.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");

        Button btnCancel = new Button("Cancelar");
        btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");

        HBox buttonRow = new HBox(15, btnSave, btnCancel);
        buttonRow.setAlignment(Pos.CENTER);

        btnSave.setOnAction(e -> {
            String name = textField.getText().trim();
            if (!name.isEmpty()) {
                try {
                    boolean inserted = new ProfessionsController().insertProfession(name);
                    if (inserted) {
                        ToastUtil.showToast(root, "✔ Profesión guardada", true);

                        // Invocas al callback correcto
                        if (callback != null) {
                            callback.accept(name);
                        }

                        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
                        pause.setOnFinished(ev -> dialog.close());
                        pause.play();
                    }
                } catch (SQLException ex) {
                    ToastUtil.showToast(root, "✖ " + ex.getMessage(), false);
                }
            } else {
                ToastUtil.showToast(root, "✖ Ingrese un nombre válido", false);
            }
        });


        btnCancel.setOnAction(e -> dialog.close());

        content.getChildren().addAll(label, textField, buttonRow);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 400, 250);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
