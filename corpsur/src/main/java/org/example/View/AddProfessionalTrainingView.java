// AddProfessionalTrainingView corregido con estilo y columnas

package org.example.View;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.example.Controller.AttendanceDetailsController;
import org.example.Controller.ProfessionalController;
import org.example.Controller.ProfessionalTrainingController;
import org.example.Model.ProfessionalModel;
import org.example.Model.ProfessionalTrainingModel;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.*;

public class AddProfessionalTrainingView {
    public static void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Asignar Doctores a Capacitación");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");
        root.setPadding(new Insets(25));

        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);

        Label lblCodigo = new Label("Código de Capacitación:");
        lblCodigo.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        TextField codeField = new TextField();
        codeField.setPromptText("Ej: ENDO2025");
        codeField.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        codeField.setMaxWidth(200);

        Button buscarBtn = new Button("Buscar capacitación");
        buscarBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6;");

        searchBox.getChildren().addAll(lblCodigo, codeField, buscarBtn);

        Label courseInfoLabel = new Label("");
        courseInfoLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13px;");
        final String[] cursoIdNombre = new String[2];

        TableView<Pair<Integer, String>> selectedTable = new TableView<>();
        selectedTable.setStyle("-fx-background-color: #0f172a;");
        selectedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        selectedTable.getStyleClass().add("dark-table");
        selectedTable.setPrefHeight(140);

        TableView<Pair<Integer, String>> professionalsTable = new TableView<>();
        professionalsTable.setStyle("-fx-background-color: #0f172a;");
        professionalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        professionalsTable.getStyleClass().add("dark-table");
        professionalsTable.setPrefHeight(180);

        TableColumn<Pair<Integer, String>, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setMinWidth(240);
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));

        TableColumn<Pair<Integer, String>, Void> actionCol = new TableColumn<>("Acciones");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final FontIcon icon = new FontIcon(FontAwesome.PLUS);
            private final Button btn = new Button("", icon);
            {
                icon.setIconColor(javafx.scene.paint.Color.web("#22c55e"));
                btn.setStyle("-fx-background-color: transparent;");
                btn.setOnAction(e -> {
                    Pair<Integer, String> item = getTableView().getItems().get(getIndex());
                    if (!selected.contains(item)) {
                        selected.add(item);
                        selectedTable.getItems().add(item);
                        btn.setDisable(true);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Pair<Integer, String> data = getTableView().getItems().get(getIndex());
                    btn.setDisable(selected.contains(data));
                    setGraphic(btn);
                    setAlignment(Pos.CENTER); // <- centra la celda
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // solo el botón
                }
            }
        });
        nameCol.setStyle("-fx-alignment: CENTER;");
        professionalsTable.getColumns().addAll(nameCol, actionCol);

        ObservableList<Pair<Integer, String>> doctorItems = FXCollections.observableArrayList();
        try {
            ProfessionalController pc = new ProfessionalController();
            for (ProfessionalModel prof : pc.getActiveProfessionals()) {
                doctorItems.add(new Pair<>(prof.getId(), prof.getNombre() + " " + prof.getApellido()));
            }
        } catch (SQLException ex) {
            ToastUtil.showToast(root, "✖ Error al cargar doctores", false);
        }
        professionalsTable.setItems(doctorItems);

        Label lblSeleccionados = new Label("Seleccionados:");
        lblSeleccionados.setStyle("-fx-text-fill: white;");

        TableColumn<Pair<Integer, String>, String> selectedNameCol = new TableColumn<>("Nombre");
        selectedNameCol.setMinWidth(240);
        selectedNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));

        TableColumn<Pair<Integer, String>, Void> deleteCol = new TableColumn<>("Acciones");
        deleteCol.setCellFactory(param -> new TableCell<>() {
            private final FontIcon icon = new FontIcon(FontAwesome.TRASH);
            private final Button btn = new Button("", icon);
            {
                icon.setIconColor(javafx.scene.paint.Color.web("#f87171"));
                btn.setStyle("-fx-background-color: transparent;");
                btn.setOnAction(e -> {
                    Pair<Integer, String> item = getTableView().getItems().get(getIndex());
                    selected.remove(item);
                    selectedTable.getItems().remove(item);
                    professionalsTable.refresh();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setAlignment(Pos.CENTER); // <- centra la celda
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // solo el botón
            }
        });
        selectedNameCol.setStyle("-fx-alignment: CENTER;");
        selectedTable.getColumns().addAll(selectedNameCol, deleteCol);

        Button inscribirBtn = new Button("Inscribir");
        inscribirBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 10;");

        inscribirBtn.setOnAction(e -> {
            if (cursoIdNombre[0] == null || selected.isEmpty()) {
                ToastUtil.showToast(root, "✖ Ingrese código válido y seleccione doctores", false);
                return;
            }
            try {
                int trainingId = Integer.parseInt(cursoIdNombre[0]);
                ProfessionalTrainingController ptc = new ProfessionalTrainingController();
                AttendanceDetailsController adc = new AttendanceDetailsController();
                List<Integer> eventos = adc.getActiveEventIdsForTraining(trainingId);

                for (Pair<Integer, String> doc : selected) {
                    if (!ptc.existsProfessionalInTraining(doc.getKey(), trainingId)) {
                        ptc.assignProfessionalToTraining(new ProfessionalTrainingModel(doc.getKey(), trainingId));
                        for (int eventId : eventos) adc.insertAttendanceDetail(eventId, doc.getKey());
                    }
                }
                ToastUtil.showToast(root, "✔ Doctores inscritos correctamente", true);
                PauseTransition pt = new PauseTransition(Duration.seconds(2));
                pt.setOnFinished(ev -> dialog.close());
                pt.play();
            } catch (Exception ex) {
                ToastUtil.showToast(root, "✖ Error al inscribir", false);
            }
        });

        buscarBtn.setOnAction(e -> {
            String code = codeField.getText().trim();
            if (!code.isEmpty()) {
                try {
                    ProfessionalTrainingController controller = new ProfessionalTrainingController();
                    String[] curso = controller.findTrainingByCode(code);
                    if (curso != null) {
                        cursoIdNombre[0] = curso[0];
                        cursoIdNombre[1] = curso[1];
                        courseInfoLabel.setText("✔ " + curso[1]);
                        courseInfoLabel.setStyle("-fx-text-fill: #22c55e;");
                    } else {
                        cursoIdNombre[0] = null;
                        cursoIdNombre[1] = null;
                        courseInfoLabel.setText("✖ Código no válido o inactivo");
                        courseInfoLabel.setStyle("-fx-text-fill: #f87171;");
                    }
                } catch (Exception ex) {
                    ToastUtil.showToast(root, "✖ Error al buscar", false);
                }
            }
        });

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
        cancelBtn.setOnAction(e -> dialog.close());

        HBox actionButtons = new HBox(15, inscribirBtn, cancelBtn);
        actionButtons.setAlignment(Pos.CENTER);

        content.getChildren().addAll(
                searchBox, courseInfoLabel,
                new Separator(),
                professionalsTable,
                lblSeleccionados,
                selectedTable,
                actionButtons
        );

        root.getChildren().add(content);
        Scene scene = new Scene(root, 560, 660);
        scene.getStylesheets().addAll(
                AddProfessionalTrainingView.class.getResource("/css/dark-theme.css").toExternalForm(),
                AddProfessionalTrainingView.class.getResource("/css/darkscroll.css").toExternalForm()
        );
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private static final ObservableList<Pair<Integer, String>> selected = FXCollections.observableArrayList();
}