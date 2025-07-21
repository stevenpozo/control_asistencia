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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EditProfesionalView {

    public interface ProfesionalUpdateCallback {
        void onProfesionalUpdated(ProfessionalModel prof);
    }

    public static void show(ProfessionalModel profModel, ProfesionalUpdateCallback callback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Profesional");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");
        root.setPadding(new Insets(30, 20, 30, 20));

        Button saveBtn = new Button("Guardar");
        saveBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;");
        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20;");

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Editar Profesional");
        title.setFont(new Font("Arial", 24));
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        vbox.getChildren().add(title);

        HBox formsRow = new HBox(30);
        formsRow.setAlignment(Pos.CENTER);
        formsRow.setPrefWidth(Double.MAX_VALUE);

        VBox personal = createSection("Información Personal");
        personal.setPrefWidth(430);
        VBox.setVgrow(personal, Priority.ALWAYS);

        VBoxGroup dniG   = createInput("Cédula", personal);
        VBoxGroup nameG  = createInput("Nombre", personal);
        VBoxGroup lastG  = createInput("Apellido", personal);
        VBoxGroup phoneG = createInput("Teléfono", personal);

        Label bLbl = new Label("Fecha de Nacimiento");
        bLbl.setStyle("-fx-text-fill:white; -fx-font-size:12px;");
        DatePicker birthPicker = new DatePicker();
        birthPicker.getStyleClass().add("date-picker");
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
        ComboBox<String> cityCb     = createComboBox("Ciudad", location);

        VBox profSec = createSection("Información Profesional");
        ComboBox<ProfessionsModel> profCb = createComboBoxModel("Profesión", profSec);

        rightCol.getChildren().addAll(location, profSec);

        HBox.setHgrow(personal, Priority.ALWAYS);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        formsRow.getChildren().addAll(personal, rightCol);
        vbox.getChildren().add(formsRow);

        HBox buttons = new HBox(20, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);
        VBox.setMargin(buttons, new Insets(10, 0, 0, 0));
        vbox.getChildren().add(buttons);

        dniG.input.setText(profModel.getCedula());
        dniG.input.setDisable(true);
        nameG.input.setText(profModel.getNombre());
        lastG.input.setText(profModel.getApellido());
        phoneG.input.setText(profModel.getTelefono());
        birthPicker.setValue(LocalDate.parse(profModel.getFechaNacimiento()));

        ProfessionalController pc = new ProfessionalController();
        List<Integer> provinceIds = new ArrayList<>();
        List<Integer> cityIds     = new ArrayList<>();

        try {
            pc.getAllProvinces().forEach(p -> {
                provinceIds.add(Integer.parseInt(p[0]));
                provinceCb.getItems().add(p[1]);
            });
            int idxProv = provinceIds.indexOf(profModel.getProvinciaId());
            if (idxProv >= 0) provinceCb.getSelectionModel().select(idxProv);

            if (idxProv >= 0) {
                pc.getCitiesByProvince(profModel.getProvinciaId()).forEach(c -> {
                    cityIds.add(Integer.parseInt(c[0]));
                    cityCb.getItems().add(c[1]);
                });
                int idxCity = cityIds.indexOf(profModel.getCiudadId());
                if (idxCity >= 0) cityCb.getSelectionModel().select(idxCity);
            }

            pc.getAllProfessions().forEach(p -> profCb.getItems().add(new ProfessionsModel(p[1], Integer.parseInt(p[0]))));
            profCb.getSelectionModel().select(
                    profCb.getItems().stream().filter(i -> i.getId() == profModel.getProfesionId()).findFirst().orElse(null)
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        provinceCb.setOnAction(e -> {
            cityCb.getItems().clear();
            cityIds.clear();
            int selIdx = provinceCb.getSelectionModel().getSelectedIndex();
            if (selIdx >= 0) {
                int pid = provinceIds.get(selIdx);
                try {
                    pc.getCitiesByProvince(pid).forEach(c -> {
                        cityIds.add(Integer.parseInt(c[0]));
                        cityCb.getItems().add(c[1]);
                    });
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Runnable updateState = () -> {
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

        cityCb.setOnAction(e -> updateState.run());
        profCb.setOnAction(e -> updateState.run());
        birthPicker.valueProperty().addListener((o, ov, nv) -> updateState.run());

        addLiveValidation(nameG, s -> s.trim().length() >= 2, "Mín 2 caracteres", updateState);
        addLiveValidation(lastG, s -> s.trim().length() >= 2, "Mín 2 caracteres", updateState);
        addLiveValidation(phoneG, s -> s.matches("\\d{7,10}"), "7–10 dígitos", updateState);

        updateState.run();

        saveBtn.setOnAction(e -> {
            profModel.setNombre(nameG.input.getText());
            profModel.setApellido(lastG.input.getText());
            profModel.setTelefono(phoneG.input.getText());
            profModel.setFechaNacimiento(birthPicker.getValue().toString());

            int selProvIdx = provinceCb.getSelectionModel().getSelectedIndex();
            if (selProvIdx >= 0) profModel.setProvinciaId(provinceIds.get(selProvIdx));

            int selCityIdx = cityCb.getSelectionModel().getSelectedIndex();
            if (selCityIdx >= 0) profModel.setCiudadId(cityIds.get(selCityIdx));

            profModel.setProfesionId(profCb.getValue().getId());

            try {
                if (pc.updateProfessional(profModel.getId(), profModel)) {
                    ToastUtil.showToast(root, "✔ Profesional actualizado", true);
                    if (callback != null) callback.onProfesionalUpdated(profModel);
                    PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
                    pt.setOnFinished(ev -> dialog.close());
                    pt.play();
                }
            } catch (SQLException ex) {
                ToastUtil.showToast(root, "✖ " + ex.getMessage(), false);
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        root.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(EditProfesionalView.class.getResource("/css/combo-style.css").toExternalForm());
        scene.getStylesheets().add(EditProfesionalView.class.getResource("/css/datepicker-dark.css").toExternalForm());
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

    private static VBox createSection(String title) {
        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        VBox box = new VBox(10, lbl);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 8;");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private static VBoxGroup createInput(String label, VBox container) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:white; -fx-font-size:12px;");
        TextField tf = new TextField();
        tf.setPromptText("Ingrese " + label.toLowerCase());
        tf.setStyle("-fx-background-color:#0f172a; -fx-text-fill:white; -fx-border-color:#374151; -fx-border-radius:8; -fx-background-radius:8;");
        tf.setMaxWidth(Double.MAX_VALUE);
        Label err = new Label();
        err.setStyle("-fx-text-fill:#facc15; -fx-font-size:11px;");
        VBox box = new VBox(3, lbl, tf, err);
        container.getChildren().add(box);
        return new VBoxGroup(tf, err, box);
    }

    private static void addLiveValidation(VBoxGroup g, java.util.function.Function<String, Boolean> validator, String errMsg, Runnable updateState) {
        g.input.textProperty().addListener((ObservableValue<? extends String> o, String oldV, String newV) -> {
            boolean ok = validator.apply(newV);
            g.error.setText(ok ? "" : errMsg);
            updateState.run();
        });
    }

    private static ComboBox<String> createComboBox(String prompt, VBox container) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setStyle("-fx-background-color: #0f172a; -fx-prompt-text-fill: white; -fx-text-fill: white; -fx-border-color: #374151; -fx-border-radius: 8; -fx-background-radius: 8;");
        cb.setMaxWidth(Double.MAX_VALUE);
        container.getChildren().add(cb);
        return cb;
    }

    private static <T> ComboBox<T> createComboBoxModel(String prompt, VBox container) {
        ComboBox<T> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setStyle("-fx-background-color: #0f172a; -fx-prompt-text-fill: white; -fx-text-fill: white; -fx-border-color: #374151; -fx-border-radius: 8; -fx-background-radius: 8;");
        cb.setMaxWidth(Double.MAX_VALUE);
        container.getChildren().add(cb);
        return cb;
    }
}
