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
import jfxtras.scene.control.CalendarPicker;
import jfxtras.scene.control.CalendarPicker.Mode;
import org.example.Controller.EventAttendanceController;
import org.example.Utils.ToastUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class AddEventAttendanceView {

    public static void show(Runnable onSuccess) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Crear Días de Asistencia");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");
        root.setPadding(new Insets(30));

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        Label labelCap = new Label("Seleccionar Capacitación");
        labelCap.setStyle("-fx-text-fill: white;");

        ComboBox<String[]> comboCap = new ComboBox<>();
        comboCap.setMaxWidth(350);

        Label fechaRangoLabel = new Label();
        fechaRangoLabel.setStyle("-fx-text-fill: #cbd5e1;");

        CalendarPicker calendarPicker = new CalendarPicker();
        calendarPicker.setMode(Mode.MULTIPLE);
        calendarPicker.setShowTime(false);
        calendarPicker.setPrefHeight(320);
        calendarPicker.setStyle("-fx-background-color: silver; -fx-border-radius: 8;");

        LocalDate[] rango = new LocalDate[2]; // fecha_inicio y fecha_fin

        try {
            List<String[]> caps = new EventAttendanceController().getActiveCapacitationsWithDateRange();
            comboCap.getItems().addAll(caps);
        } catch (SQLException e) {
            ToastUtil.showToast(root, "✖ Error al cargar capacitaciones", false);
        }

        comboCap.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item[1] + " - " + item[2]);
            }
        });

        comboCap.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item[1] + " - " + item[2]);
            }
        });

        comboCap.setOnAction(e -> {
            String[] selected = comboCap.getValue();
            if (selected != null) {
                rango[0] = LocalDate.parse(selected[3]);
                rango[1] = LocalDate.parse(selected[4]);

                fechaRangoLabel.setText("Asistencia válida del " + rango[0] + " al " + rango[1]);
                calendarPicker.setDisplayedCalendar(Calendar.getInstance()); // reset visual
                calendarPicker.calendars().clear(); // limpia selección previa
            }
        });

        Button btnCrear = new Button("Crear días seleccionados");
        btnCrear.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 8 20;");
        HBox botones = new HBox(15, btnCrear, btnCancelar);
        botones.setAlignment(Pos.CENTER);

        btnCrear.setOnAction(ev -> {
            String[] selected = comboCap.getValue();
            if (selected == null || rango[0] == null || rango[1] == null) {
                ToastUtil.showToast(root, "✖ Seleccione una capacitación válida", false);
                return;
            }

            int capId = Integer.parseInt(selected[0]);
            Set<LocalDate> fechasSeleccionadas = calendarPicker.calendars().stream()
                    .map(c -> LocalDate.ofInstant(c.toInstant(), ZoneId.systemDefault()))
                    .filter(date -> !date.isBefore(rango[0]) && !date.isAfter(rango[1]))
                    .collect(Collectors.toSet());

            if (fechasSeleccionadas.isEmpty()) {
                ToastUtil.showToast(root, "✖ Seleccione días válidos en el rango", false);
                return;
            }

            EventAttendanceController controller = new EventAttendanceController();
            int guardados = 0;
            int duplicados = 0;

            for (LocalDate fecha : fechasSeleccionadas) {
                String fechaStr = fecha.toString();
                try {
                    if (controller.dayExists(capId, fechaStr)) {
                        duplicados++;
                    } else if (controller.createDay(capId, fechaStr)) {
                        guardados++;
                    }
                } catch (SQLException ex) {
                    ToastUtil.showToast(root, "✖ " + ex.getMessage(), false);
                }
            }

            if (guardados > 0) {
                ToastUtil.showToast(root, "✔ Se crearon " + guardados + " asistencias", true);
                PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
                pause.setOnFinished(end -> {
                    dialog.close();
                    if (onSuccess != null) onSuccess.run();
                });
                pause.play();
            } else {
                ToastUtil.showToast(root, "No se crearon nuevas asistencias. (" + duplicados + " duplicadas)", false);
            }
        });

        btnCancelar.setOnAction(e -> dialog.close());

        content.getChildren().addAll(labelCap, comboCap, fechaRangoLabel, calendarPicker, botones);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 500, 560);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
