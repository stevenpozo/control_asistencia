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
import org.example.Controller.TrainingController;
import org.example.Model.LaboratoryModel;
import org.example.Model.TrainingModel;
import org.example.Utils.ToastUtil;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class AddTrainingView {

    public interface TrainingCallback {
        void onTrainingCreated(TrainingModel training);
    }

    public static void show(TrainingCallback callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nueva Capacitación");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);

        // Ícono
        InputStream iconStream = AddTrainingView.class.getResourceAsStream("/img/training-icon.png");
        if (iconStream != null) {
            ImageView icon = new ImageView(new Image(iconStream));
            icon.setFitWidth(50);
            icon.setFitHeight(50);
            content.getChildren().add(icon);
        }

        Label labelNombre = new Label("Nombre de la capacitación");
        labelNombre.setStyle("-fx-text-fill: white;");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Capacitación Odontológica 2025");
        txtNombre.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white;");
        txtNombre.setMaxWidth(280);

        Label labelCodigo = new Label("Código de la capacitación");
        labelCodigo.setStyle("-fx-text-fill: white;");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Ej: COD-001");
        txtCodigo.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white;");
        txtCodigo.setMaxWidth(280);

        Label labelInicio = new Label("Fecha de inicio");
        labelInicio.setStyle("-fx-text-fill: white;");
        DatePicker dateInicio = new DatePicker();

        Label labelFin = new Label("Fecha de fin");
        labelFin.setStyle("-fx-text-fill: white;");
        DatePicker dateFin = new DatePicker();

        Label labelLaboratorio = new Label("Seleccionar Laboratorio");
        labelLaboratorio.setStyle("-fx-text-fill: white;");
        ComboBox<LaboratoryModel> cmbLaboratorio = new ComboBox<>();
        cmbLaboratorio.setMaxWidth(280);

        try {
            List<LaboratoryModel> labs = new LaboratoryController().getActiveLaboratories();
            cmbLaboratorio.getItems().addAll(labs);
        } catch (SQLException e) {
            ToastUtil.showToast(root, "✖ Error al cargar laboratorios", false);
        }

        cmbLaboratorio.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(LaboratoryModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cmbLaboratorio.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LaboratoryModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");

        HBox botones = new HBox(15, btnGuardar, btnCancelar);
        botones.setAlignment(Pos.CENTER);

        btnGuardar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            String codigo = txtCodigo.getText().trim();
            String inicio = dateInicio.getValue() != null ? dateInicio.getValue().toString() : "";
            String fin = dateFin.getValue() != null ? dateFin.getValue().toString() : "";
            LaboratoryModel lab = cmbLaboratorio.getValue();

            if (nombre.isEmpty() || codigo.isEmpty() || inicio.isEmpty() || fin.isEmpty() || lab == null) {
                ToastUtil.showToast(root, "✖ Complete todos los campos", false);
                return;
            }

            TrainingModel model = new TrainingModel();
            model.setNombre(nombre);
            model.setCodigo(codigo);
            model.setFechaInicio(inicio);
            model.setFechaFin(fin);
            model.setLaboratorioId(lab.getId());

            try {
                boolean success = new TrainingController().insertTraining(model);
                if (success) {
                    ToastUtil.showToast(root, "✔ Capacitación guardada", true);
                    if (callback != null) callback.onTrainingCreated(model);
                    PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
                    pause.setOnFinished(ev -> dialog.close());
                    pause.play();
                }
            } catch (SQLException ex) {
                ToastUtil.showToast(root, "✖ " + ex.getMessage(), false);
            }
        });

        btnCancelar.setOnAction(e -> dialog.close());

        content.getChildren().addAll(
                labelNombre, txtNombre,
                labelCodigo, txtCodigo,
                labelInicio, dateInicio,
                labelFin, dateFin,
                labelLaboratorio, cmbLaboratorio,
                botones
        );
        root.getChildren().add(content);

        Scene scene = new Scene(root, 430, 550);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
