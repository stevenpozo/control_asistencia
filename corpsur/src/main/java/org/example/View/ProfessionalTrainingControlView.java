// ProfessionalTrainingControlView con estilo oscuro, datos centrados y progreso estilizado

package org.example.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Controller.AttendanceDetailsController;
import org.example.Controller.ProfessionalTrainingController;
import org.example.Utils.ToastUtil;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ProfessionalTrainingControlView {

    public static void show(int trainingId, String nombreCurso, Date fechaInicio, Date fechaFin) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Control de Asistencia - " + nombreCurso);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Profesionales Inscritos en: " + nombreCurso);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<String[]> table = new TableView<>();
        table.setPrefHeight(400);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("dark-table");

        String[] headers = {"Nombre", "Apellido", "Cédula", "Nacimiento", "Teléfono", "Progreso", "Estado Diario"};
        int[] widths = {120, 120, 100, 110, 100, 100, 130};

        for (int i = 0; i < headers.length; i++) {
            final int index = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setMinWidth(widths[i]);
            col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[index]));
            col.setStyle("-fx-alignment: CENTER;");

            // Progreso personalizado
            if (index == 5) {
                col.setCellFactory(column -> new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            if (item.toLowerCase().contains("finalizado")) {
                                setText("Finalizado");
                                setStyle("-fx-font-weight: bold; -fx-alignment: CENTER; -fx-text-fill: #fca5a5;");
                            } else {
                                setText("Cursando");
                                setStyle("-fx-font-weight: bold; -fx-alignment: CENTER; -fx-text-fill: #4ade80;");
                            }
                        }
                    }
                });
            }
            table.getColumns().add(col);
        }


        ObservableList<String[]> data = FXCollections.observableArrayList();

        try {
            ProfessionalTrainingController ptc = new ProfessionalTrainingController();
            AttendanceDetailsController adc = new AttendanceDetailsController();

            List<String[]> profesionales = ptc.getProfessionalsByTraining(trainingId);
            LocalDate today = LocalDate.now();

            boolean cursoActivo = !today.isBefore(fechaInicio.toLocalDate()) && !today.isAfter(fechaFin.toLocalDate());
            boolean cursoFinalizado = today.isAfter(fechaFin.toLocalDate());

            for (String[] prof : profesionales) {
                String cedula = prof[0];
                String nombre = prof[1];
                String apellido = prof[2];
                String nacimiento = prof[3];
                String telefono = prof[4];
                boolean activo = "Activo".equals(prof[5]);

                String progreso = cursoFinalizado ? "Finalizado" : (cursoActivo ? "Cursando" : "Pendiente");

                String estado;
                if (!activo) {
                    estado = "Inactivo";
                } else {
                    estado = adc.getEstadoGeneralDelDoctor(trainingId, cedula);
                }

                data.add(new String[]{
                        nombre, apellido, cedula, nacimiento, telefono, progreso, estado
                });
            }

        } catch (SQLException e) {
            ToastUtil.showToast(root, "✖ Error al cargar datos", false);
        }

        table.setItems(data);

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20;");
        btnCerrar.setOnAction(e -> dialog.close());

        content.getChildren().addAll(titleLabel, table, btnCerrar);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 850, 550);
        scene.getStylesheets().addAll(
                ProfessionalTrainingControlView.class.getResource("/css/dark-theme.css").toExternalForm(),
                ProfessionalTrainingControlView.class.getResource("/css/darkscroll.css").toExternalForm()
        );

        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
