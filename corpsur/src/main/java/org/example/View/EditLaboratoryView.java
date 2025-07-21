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
import org.example.Controller.LaboratoryController;
import org.example.Model.LaboratoryModel;
import org.example.Utils.ToastUtil;

import java.io.InputStream;
import java.sql.SQLException;

public class EditLaboratoryView {

    public static void show(LaboratoryModel labToEdit, Runnable onSuccess) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Laboratorio");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        InputStream iconStream = EditLaboratoryView.class.getResourceAsStream("/img/lab-icon.png");
        if (iconStream != null) {
            ImageView labIcon = new ImageView(new Image(iconStream));
            labIcon.setFitWidth(50);
            labIcon.setFitHeight(50);
            content.getChildren().add(labIcon);
        }

        Label label = new Label("Editar nombre del laboratorio");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField textField = new TextField(labToEdit.getName());
        textField.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        textField.setMaxWidth(280);

        Button btnUpdate = new Button("Actualizar");
        btnUpdate.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");

        Button btnCancel = new Button("Cancelar");
        btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");

        HBox buttonRow = new HBox(15, btnUpdate, btnCancel);
        buttonRow.setAlignment(Pos.CENTER);

        btnUpdate.setOnAction(e -> {
            String newName = textField.getText().trim();
            if (!newName.isEmpty()) {
                try {
                    boolean success = new LaboratoryController().updateLaboratory(labToEdit.getId(), newName);
                    if (success) {
                        ToastUtil.showToast(root, "✔ Laboratorio actualizado", true);
                        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
                        pause.setOnFinished(ev -> {
                            dialog.close();
                            onSuccess.run(); // Para refrescar tabla, si se requiere
                        });
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

        Scene scene = new Scene(root, 400, 280);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
