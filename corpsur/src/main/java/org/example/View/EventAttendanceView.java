package org.example.View;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.Controller.EventAttendanceController;
import org.example.Model.EventAttendanceModel;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EventAttendanceView {

    private TableView<EventAttendanceModel> table;
    private EventAttendanceController controller;
    private StackPane rootStackPane;
    private ObservableList<EventAttendanceModel> data;
    private DatePicker datePicker;
    private ComboBox<String[]> capFilterCombo;

    public StackPane getView() {
        controller = new EventAttendanceController();
        controllerAutoClose();

        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.setMaxWidth(1100);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Eventos de Asistencia por Fecha");
        title.setFont(Font.font("Segoe UI", 17));
        title.setStyle("-fx-text-fill: white;");

        FontIcon addIcon = new FontIcon(FontAwesome.PLUS);
        addIcon.setIconSize(16);
        addIcon.setIconColor(Color.WHITE);
        Button addButton = new Button("", addIcon);
        addButton.setStyle("-fx-background-color: #334155; -fx-background-radius: 8;");
        addButton.setOnAction(e -> showAddDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        datePicker = new DatePicker();
        datePicker.setStyle("-fx-background-color: #334155; -fx-text-fill: white;");
        datePicker.setOnAction(e -> refreshTable());

        capFilterCombo = new ComboBox<>();
        capFilterCombo.setPromptText("Filtrar por capacitación");
        capFilterCombo.setStyle("-fx-background-radius: 6; -fx-background-color: #334155; -fx-text-fill: white;");
        capFilterCombo.setMinWidth(250);

        try {
            List<String[]> caps = controller.getCapacitationFilterList();
            capFilterCombo.getItems().addAll(caps);
        } catch (SQLException e) {
            ToastUtil.showToast(null, "✖ No se pudieron cargar capacitaciones", false);
        }

        capFilterCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item[1]);
            }
        });
        capFilterCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item[1]);
                setStyle("-fx-text-fill: white;");
            }
        });

        capFilterCombo.setOnAction(e -> refreshTable());

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 5 14;");
        btnLimpiar.setOnAction(e -> {
            datePicker.setValue(null);
            capFilterCombo.setValue(null);
            refreshTable();
        });

        HBox topControls = new HBox(10, title, addButton, spacer, new Label("Fecha:"), datePicker, capFilterCombo, btnLimpiar);
        topControls.setAlignment(Pos.CENTER_LEFT);

        table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setPlaceholder(new Label("No hay asistencias para la selección actual"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EventAttendanceModel, String> colNombre = new TableColumn<>("Capacitación");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombreCapacitacion()));

        TableColumn<EventAttendanceModel, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCodigoCapacitacion()));

        TableColumn<EventAttendanceModel, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFecha()));

        TableColumn<EventAttendanceModel, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().isCerrado() ? "Cerrado" : "Abierto"));

        TableColumn<EventAttendanceModel, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(10);
            private final FontIcon switchIcon = new FontIcon();

            {
                switchIcon.setIconSize(22);
                switchIcon.setOnMouseClicked(e -> {
                    EventAttendanceModel event = getTableView().getItems().get(getIndex());
                    showStatusConfirmation(event);
                });

                actionBox.getChildren().add(switchIcon);
                actionBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    EventAttendanceModel event = getTableView().getItems().get(getIndex());
                    if (event.isCerrado()) {
                        switchIcon.setIconLiteral("fa-toggle-off");
                        switchIcon.setIconColor(Color.web("#f87171"));
                    } else {
                        switchIcon.setIconLiteral("fa-toggle-on");
                        switchIcon.setIconColor(Color.web("#4ade80"));
                    }
                    setGraphic(actionBox);
                }
            }
        });

        table.getColumns().addAll(colNombre, colCodigo, colFecha, colEstado, colAcciones);
        centerColumn(colNombre);
        centerColumn(colCodigo);
        centerColumn(colFecha);
        centerColumn(colEstado);

        VBox.setMargin(table, new Insets(10, 0, 0, 0));
        card.getChildren().addAll(topControls, table);

        rootStackPane = new StackPane();
        rootStackPane.setStyle("-fx-background-color: #0f172a;");
        rootStackPane.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
        StackPane.setMargin(card, new Insets(40, 0, 40, 0));

        String theme = getClass().getResource("/css/dark-theme.css").toExternalForm();
        String scroll = getClass().getResource("/css/darkscroll.css").toExternalForm();
        rootStackPane.getStylesheets().addAll(theme, scroll);

        refreshTable();
        return rootStackPane;
    }

    private void refreshTable() {
        try {
            List<EventAttendanceModel> list;

            if (capFilterCombo.getValue() != null) {
                String[] cap = capFilterCombo.getValue();
                list = controller.getEventAttendancesByCapId(Integer.parseInt(cap[0]));
            } else if (datePicker.getValue() != null) {
                list = controller.getEventAttendancesByDate(datePicker.getValue().toString());
            } else {
                list = controller.getAllEventAttendancesOrderedByDate();
            }

            data = FXCollections.observableArrayList(list);
            table.setItems(data);

            table.setFixedCellSize(40);
            table.prefHeightProperty().bind(
                    table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01))
            );
        } catch (SQLException e) {
            ToastUtil.showToast(rootStackPane, "✖ Error al cargar eventos", false);
        }
    }

    private void showStatusConfirmation(EventAttendanceModel event) {
        boolean nuevoEstado = !event.isCerrado();
        StatusEventAttendanceView.show(
                event.getNombreCapacitacion(),
                event.getFecha(),
                nuevoEstado,
                confirmed -> {
                    if (confirmed) {
                        try {
                            boolean updated = nuevoEstado
                                    ? controller.openEvent(event.getId())
                                    : controller.closeEvent(event.getId());

                            if (updated) {
                                ToastUtil.showToastTopRight(
                                        rootStackPane,
                                        nuevoEstado ? "✔ Asistencia abierta" : "✔ Asistencia cerrada",
                                        true
                                );
                                refreshTable();
                            }
                        } catch (SQLException e) {
                            ToastUtil.showToastTopRight(rootStackPane, "✖ " + e.getMessage(), false);
                        }
                    }
                    return null;
                }
        );
    }

    private void controllerAutoClose() {
        try {
            controller.autoClosePastEvents();
        } catch (SQLException e) {
            System.err.println("⚠ Error en cierre automático: " + e.getMessage());
        }
    }

    private <T> void centerColumn(TableColumn<EventAttendanceModel, T> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void showAddDialog() {
        AddEventAttendanceView.show(this::refreshTable);
    }
}
