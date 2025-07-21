package org.example.View;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.scene.control.CalendarPicker;
import jfxtras.scene.control.CalendarPicker.Mode;
import org.example.Controller.AsistenciasController;
import org.example.Utils.ToastUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

public class AddAsistenciasView {

    public interface AttendanceCallback {
        Void call(boolean success);
    }

    public static void show(AttendanceCallback callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Generar Eventos de Asistencia");

        StackPane root = new StackPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(400);

        Label label = new Label("Selecciona fechas para generar eventos:");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        CalendarPicker picker = new CalendarPicker();
        picker.getStyleClass().add("calendar-picker");
        picker.setMode(Mode.MULTIPLE);
        picker.setShowTime(false);
        picker.setPrefHeight(300);
        picker.setStyle(
                "-fx-background-color: silver; " +
                        "-fx-border-color: white; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8;"
        );
        Button btnGenerate = new Button("Generar Eventos");
        btnGenerate.setStyle(
                "-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;"
        );
        btnGenerate.setOnAction(e -> {
            Set<LocalDate> fechas = picker.calendars().stream()
                    .map(c -> LocalDate.ofInstant(c.toInstant(), ZoneId.systemDefault()))
                    .collect(Collectors.toSet());
            try {
                AsistenciasController ctr = new AsistenciasController();
                boolean allOk = true;
                for (LocalDate f : fechas) {
                    if (!ctr.generarEventosParaFecha(f)) allOk = false;
                }
                if (allOk) {
                    ToastUtil.showToast(root, "✔ Eventos generados", true);
                    if (callback != null) callback.call(true);
                    PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
                    pt.setOnFinished(ev -> dialog.close());
                    pt.play();
                } else {
                    ToastUtil.showToast(root, "✖ Algunos eventos no se generaron", false);
                }
            } catch (SQLException ex) {
                ToastUtil.showToast(root, "✖ " + ex.getMessage(), false);
            }
        });

        Button btnCancel = new Button("Cancelar");
        btnCancel.setStyle(
                "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;"
        );
        btnCancel.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(15, btnGenerate, btnCancel);
        buttons.setAlignment(Pos.CENTER);

        content.getChildren().addAll(label, picker, buttons);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 450, 420);
        scene.getStylesheets().add(
                AddAsistenciasView.class.getResource("/css/calendar.css").toExternalForm()
        );
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
