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

public class EditTrainingView {

    public static void show(TrainingModel trainingToEdit, Runnable onSuccess) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Capacitación");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);

        InputStream iconStream = EditTrainingView.class.getResourceAsStream("/img/training-icon.png");
        if (iconStream != null) {
            ImageView icon = new ImageView(new Image(iconStream));
            icon.setFitWidth(50);
            icon.setFitHeight(50);
            content.getChildren().add(icon);
        }

        Label lblNombre = new Label("Nombre de la capacitación");
        lblNombre.setStyle("-fx-text-fill: white;");
        TextField txtNombre = new TextField(trainingToEdit.getNombre());
        txtNombre.setMaxWidth(280);
        txtNombre.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white;");

        Label lblCodigo = new Label("Código");
        lblCodigo.setStyle("-fx-text-fill: white;");
        TextField txtCodigo = new TextField(trainingToEdit.getCodigo());
        txtCodigo.setMaxWidth(280);
        txtCodigo.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white;");

        Label lblInicio = new Label("Fecha de inicio");
        lblInicio.setStyle("-fx-text-fill: white;");
        DatePicker dateInicio = new DatePicker();
        dateInicio.setValue(java.time.LocalDate.parse(trainingToEdit.getFechaInicio()));

        Label lblFin = new Label("Fecha de fin");
        lblFin.setStyle("-fx-text-fill: white;");
        DatePicker dateFin = new DatePicker();
        dateFin.setValue(java.time.LocalDate.parse(trainingToEdit.getFechaFin()));

        Label lblLab = new Label("Laboratorio");
        lblLab.setStyle("-fx-text-fill: white;");
        ComboBox<LaboratoryModel> cmbLab = new ComboBox<>();
        cmbLab.setMaxWidth(280);

        try {
            List<LaboratoryModel> labs = new LaboratoryController().getActiveLaboratories();
            cmbLab.getItems().addAll(labs);
            labs.stream()
                    .filter(l -> l.getId() == trainingToEdit.getLaboratorioId())
                    .findFirst()
                    .ifPresent(cmbLab::setValue);
        } catch (SQLException e) {
            ToastUtil.showToast(root, "✖ Error al cargar laboratorios", false);
        }

        cmbLab.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(LaboratoryModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cmbLab.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LaboratoryModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        Button btnActualizar = new Button("Actualizar");
        btnActualizar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");

        HBox btns = new HBox(15, btnActualizar, btnCancelar);
        btns.setAlignment(Pos.CENTER);

        btnActualizar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            String codigo = txtCodigo.getText().trim();
            String inicio = dateInicio.getValue() != null ? dateInicio.getValue().toString() : "";
            String fin = dateFin.getValue() != null ? dateFin.getValue().toString() : "";
            LaboratoryModel lab = cmbLab.getValue();

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
                boolean success = new TrainingController().updateTraining(trainingToEdit.getId(), model);
                if (success) {
                    ToastUtil.showToast(root, "✔ Capacitación actualizada", true);
                    PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
                    pause.setOnFinished(ev -> {
                        dialog.close();
                        if (onSuccess != null) onSuccess.run();
                    });
                    pause.play();
                }
            } catch (SQLException ex) {
                ToastUtil.showToast(root, "✖ " + ex.getMessage(), false);
            }
        });

        btnCancelar.setOnAction(e -> dialog.close());

        content.getChildren().addAll(
                lblNombre, txtNombre,
                lblCodigo, txtCodigo,
                lblInicio, dateInicio,
                lblFin, dateFin,
                lblLab, cmbLab,
                btns
        );

        root.getChildren().add(content);

        Scene scene = new Scene(root, 430, 540);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
