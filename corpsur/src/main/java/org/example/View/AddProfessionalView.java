package org.example.View;

import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.Controller.ProfessionalController;
import org.example.Model.ProfessionsModel;
import org.example.Model.ProfessionalModel;
import org.example.Utils.Validations;
import org.example.Utils.ToastUtil;

import java.sql.SQLException;
import java.util.function.Function;

public class AddProfessionalView {

    public interface ProfesionalCallback {
        void onProfesionalCreated(ProfessionalModel prof);
    }

    public static void show(ProfesionalCallback callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Registro Profesional");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");
        root.setPadding(new Insets(30, 20, 30, 20));

        Button saveBtn = new Button("Guardar");
        saveBtn.setDisable(true);
        saveBtn.setStyle(
                "-fx-background-color: #10b981; -fx-text-fill: white;" +
                        "-fx-background-radius: 8; -fx-padding: 8 20;"
        );

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Registro Profesional");
        title.setFont(new Font("Arial", 24));
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        vbox.getChildren().add(title);

        HBox formsRow = new HBox(30);
        formsRow.setAlignment(Pos.CENTER);
        formsRow.setPrefWidth(Double.MAX_VALUE);

        VBox personal = createSection("Información Personal");
        personal.setPrefWidth(430);
        VBox.setVgrow(personal, Priority.ALWAYS);

        VBoxGroup dniG = createInput("Cédula", personal);
        VBoxGroup nameG = createInput("Nombre", personal);
        VBoxGroup lastG = createInput("Apellido", personal);
        VBoxGroup phoneG = createInput("Teléfono", personal);

        Label bLbl = new Label("Fecha de Nacimiento");
        bLbl.setStyle("-fx-text-fill:white; -fx-font-size:12px;");
        DatePicker birthPicker = new DatePicker();
        birthPicker.getStyleClass().add("date-picker");
        birthPicker.setPromptText("Selecciona fecha");
        birthPicker.setMaxWidth(Double.MAX_VALUE);
        VBox birthBox = new VBox(5, bLbl, birthPicker);
        birthBox.setPadding(new Insets(5, 0, 0, 0));
        personal.getChildren().add(birthBox);

        VBox rightCol = new VBox(20);
        rightCol.setAlignment(Pos.TOP_CENTER);
        rightCol.setPrefWidth(430);
        VBox.setVgrow(rightCol, Priority.ALWAYS);

        VBox location = createSection("Ubicación y Dirección");
        ComboBox<String> provinceCb = createComboBox("Provincia", location);
        ComboBox<String> cityCb = createComboBox("Ciudad", location);

        VBox profSec = createSection("Información Profesional");
        ComboBox<ProfessionsModel> profCb = createComboBoxModel("Profesión", profSec);

        rightCol.getChildren().addAll(location, profSec);

        HBox.setHgrow(personal, Priority.ALWAYS);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        formsRow.getChildren().addAll(personal, rightCol);
        vbox.getChildren().add(formsRow);

        Runnable updateSaveState = () -> {
            boolean ok =
                    Validations.isValidEcuadorianDNI(dniG.input.getText()) &&
                            nameG.error.getText().isEmpty() &&
                            lastG.error.getText().isEmpty() &&
                            phoneG.error.getText().isEmpty() &&
                            !provinceCb.getSelectionModel().isEmpty() &&
                            !cityCb.getSelectionModel().isEmpty() &&
                            !profCb.getSelectionModel().isEmpty() &&
                            birthPicker.getValue() != null;
            saveBtn.setDisable(!ok);
        };

        ProfessionalController pc = new ProfessionalController();
        try {
            pc.getAllProvinces().forEach(p -> provinceCb.getItems().add(p[1]));
            pc.getAllProfessions().forEach(p ->
                    profCb.getItems().add(new ProfessionsModel(p[1], Integer.parseInt(p[0])))
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        provinceCb.setOnAction(e -> {
            cityCb.getItems().clear();
            try {
                int pid = provinceCb.getSelectionModel().getSelectedIndex() + 1;
                pc.getCitiesByProvince(pid).forEach(c -> cityCb.getItems().add(c[1]));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            updateSaveState.run();
        });
        cityCb.setOnAction(e -> updateSaveState.run());
        profCb.setOnAction(e -> updateSaveState.run());
        birthPicker.valueProperty().addListener((o, ov, nv) -> updateSaveState.run());

        addLiveValidation(dniG, Validations::isValidEcuadorianDNI, "Cédula inválida", updateSaveState);
        addLiveValidation(nameG, s -> s.trim().length() >= 2, "Mín 2 caracteres", updateSaveState);
        addLiveValidation(lastG, s -> s.trim().length() >= 2, "Mín 2 caracteres", updateSaveState);
        addLiveValidation(phoneG, s -> s.matches("\\d{7,10}"), "7–10 dígitos", updateSaveState);

        HBox buttons = new HBox(20, saveBtn, new Button("Cancelar"));
        buttons.setAlignment(Pos.CENTER);
        VBox.setMargin(buttons, new Insets(10, 0, 0, 0));
        vbox.getChildren().add(buttons);

        saveBtn.setOnAction(e -> {
            ProfessionalModel p = new ProfessionalModel();
            p.setCedula(dniG.input.getText());
            p.setNombre(nameG.input.getText());
            p.setApellido(lastG.input.getText());
            p.setTelefono(phoneG.input.getText());
            p.setFechaNacimiento(birthPicker.getValue().toString());
            p.setProvinciaId(provinceCb.getSelectionModel().getSelectedIndex() + 1);
            p.setCiudadId(cityCb.getSelectionModel().getSelectedIndex() + 1);
            p.setProfesionId(profCb.getValue().getId());
            p.setActivo(true);
            try {
                if (pc.insertProfessional(p)) {
                    ToastUtil.showToast(root, "✔ Profesional guardado", true);
                    if (callback != null) callback.onProfesionalCreated(p);
                    PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
                    pt.setOnFinished(ev -> dialog.close());
                    pt.play();
                }
            } catch (SQLException ex) {
                ToastUtil.showToast(root, "✖ " + ex.getMessage(), false);
            }
        });

        root.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
                AddProfessionalView.class.getResource("/css/combo-style.css").toExternalForm()
        );
        scene.getStylesheets().add(
                AddProfessionalView.class.getResource("/css/datepicker-dark.css").toExternalForm()
        );
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private static class VBoxGroup {
        final TextField input;
        final Label error;
        final VBox box;

        VBoxGroup(TextField i, Label e, VBox b) {
            input = i;
            error = e;
            box = b;
        }
    }

    private static VBoxGroup createInput(String label, VBox container) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:white; -fx-font-size:12px;");
        TextField tf = new TextField();
        tf.setPromptText("Ingrese " + label.toLowerCase());
        tf.setStyle(
                "-fx-background-color:#0f172a; -fx-text-fill:white;" +
                        "-fx-border-color:#374151; -fx-border-radius:8;" +
                        "-fx-background-radius:8;"
        );
        tf.setMaxWidth(Double.MAX_VALUE);

        Label err = new Label();
        err.setStyle("-fx-text-fill:#facc15; -fx-font-size:11px;");

        VBox box = new VBox(3, lbl, tf, err);
        container.getChildren().add(box);
        return new VBoxGroup(tf, err, box);
    }

    private static void addLiveValidation(VBoxGroup g,
                                          Function<String, Boolean> validator,
                                          String errMsg,
                                          Runnable updateState) {
        g.input.textProperty().addListener((ObservableValue<? extends String> o,
                                            String oldV, String newV) -> {
            boolean ok = validator.apply(newV);
            g.error.setText(ok ? "" : errMsg);
            updateState.run();
        });
    }

    private static ComboBox<String> createComboBox(String prompt, VBox container) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setStyle(
                "-fx-background-color: #0f172a; -fx-prompt-text-fill: white; -fx-text-fill: white; " +
                        "-fx-border-color: #374151; -fx-border-radius: 8; -fx-background-radius: 8;"
        );
        cb.setMaxWidth(Double.MAX_VALUE);
        container.getChildren().add(cb);
        return cb;
    }

    private static <T> ComboBox<T> createComboBoxModel(String prompt, VBox container) {
        ComboBox<T> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setStyle(
                "-fx-background-color: #0f172a; -fx-prompt-text-fill: white; -fx-text-fill: white; " +
                        "-fx-border-color: #374151; -fx-border-radius: 8; -fx-background-radius: 8;"
        );
        cb.setMaxWidth(Double.MAX_VALUE);
        container.getChildren().add(cb);
        return cb;
    }

    private static VBox createSection(String title) {
        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        VBox box = new VBox(10, lbl);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 8;");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }
}
