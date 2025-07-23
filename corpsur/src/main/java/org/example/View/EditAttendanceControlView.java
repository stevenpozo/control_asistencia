package org.example.View;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.Controller.AttendanceControlController;
import org.example.Model.AttendanceControlModel;
import org.example.Utils.ToastUtil;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EditAttendanceControlView {

    public interface AttendanceUpdateCallback {
        void onAttendanceUpdated(AttendanceControlModel model);
    }

    public static void show(AttendanceControlModel model, AttendanceUpdateCallback callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Asistencia");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        root.setPadding(new Insets(30));

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);

        Label title = new Label("Editar Horas de Asistencia");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox form = new VBox(15);
        form.setMaxWidth(350);

        TimePickerField entrada     = createTimeField("Hora de Entrada", model.getHoraEntrada());
        TimePickerField salidaAlm   = createTimeField("Salida a Almuerzo", model.getHoraSalidaAlmuerzo());
        TimePickerField regresoAlm  = createTimeField("Regreso de Almuerzo", model.getHoraRegresoAlmuerzo());
        TimePickerField salidaFinal = createTimeField("Salida Final", model.getHoraSalidaFinal());

        form.getChildren().addAll(entrada.box, salidaAlm.box, regresoAlm.box, salidaFinal.box);

        // Validación por estado
        boolean asistenciaCerrada = model.getEstado() == 0;

        if (asistenciaCerrada) {
            entrada.input.setDisable(true);
            salidaAlm.input.setDisable(true);
            regresoAlm.input.setDisable(true);
            salidaFinal.input.setDisable(true);

            Label estadoLabel = new Label("⚠ Esta asistencia ya está cerrada y no se puede editar.");
            estadoLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 13px;");
            form.getChildren().add(estadoLabel);
        }

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button guardarBtn = new Button("Guardar");
        guardarBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;");
        Button cancelarBtn = new Button("Cancelar");
        cancelarBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;");

        guardarBtn.setDisable(asistenciaCerrada); // también deshabilitamos el botón

        buttons.getChildren().addAll(guardarBtn, cancelarBtn);

        container.getChildren().addAll(title, form, buttons);
        root.getChildren().add(container);

        guardarBtn.setOnAction(e -> {
            try {
                AttendanceControlController controller = new AttendanceControlController();
                boolean ok = controller.updateAsistenciaManual(
                        model.getAsistenciaDetalleId(),
                        entrada.getValue(), salidaAlm.getValue(),
                        regresoAlm.getValue(), salidaFinal.getValue()
                );
                if (ok) {
                    model.setHoraEntrada(entrada.getValue());
                    model.setHoraSalidaAlmuerzo(salidaAlm.getValue());
                    model.setHoraRegresoAlmuerzo(regresoAlm.getValue());
                    model.setHoraSalidaFinal(salidaFinal.getValue());
                    ToastUtil.showToast(root, "✔ Asistencia actualizada", true);
                    if (callback != null) callback.onAttendanceUpdated(model);
                    PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
                    pt.setOnFinished(ev -> dialog.close());
                    pt.play();
                }
            } catch (SQLException ex) {
                ToastUtil.showToast(root, "✖ " + ex.getMessage(), false);
                ex.printStackTrace();
            }
        });

        cancelarBtn.setOnAction(e -> dialog.close());

        Scene scene = new Scene(root, 480, 500);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private static class TimePickerField {
        final Label label;
        final TextField input;
        final VBox box;

        TimePickerField(String title, LocalTime time) {
            label = new Label(title);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            input = new TextField();
            input.setPromptText("HH:mm");
            input.setText(time != null ? time.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
            input.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-border-color: #374151; -fx-border-radius: 8; -fx-background-radius: 8;");
            input.setMaxWidth(Double.MAX_VALUE);
            box = new VBox(5, label, input);
        }

        LocalTime getValue() {
            String text = input.getText().trim();
            if (text.isEmpty()) return null;
            try {
                return LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static TimePickerField createTimeField(String label, LocalTime time) {
        return new TimePickerField(label, time);
    }
}
