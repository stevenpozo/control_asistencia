package org.example.View;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.Controller.AttendanceControlController;
import org.example.Model.AttendanceControlModel;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceControlView {

    private TableView<AttendanceControlModel> table;
    private AttendanceControlController controller;
    private StackPane root;
    private ComboBox<String> capacitacionFilter;
    private ObservableList<AttendanceControlModel> data;
    private FilteredList<AttendanceControlModel> filteredData;

    public StackPane getView() {
        controller = new AttendanceControlController();

        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.setMaxWidth(1300);

        Label title = new Label("Control de Asistencias");
        title.setFont(Font.font("Segoe UI", 18));
        title.setStyle("-fx-text-fill: white;");

        Button allBtn = new Button("Todas");
        Button activasBtn = new Button("Activas");
        Button cerradasBtn = new Button("Finalizadas");
        Button hoyBtn = new Button("Hoy");
        styleFilterButton(allBtn);
        styleFilterButton(activasBtn);
        styleFilterButton(cerradasBtn);
        styleFilterButton(hoyBtn);

        allBtn.setOnAction(e -> refreshAll());
        activasBtn.setOnAction(e -> refreshByEstado(false));
        cerradasBtn.setOnAction(e -> refreshByEstado(true));
        hoyBtn.setOnAction(e -> refreshToday());

        capacitacionFilter = new ComboBox<>();
        capacitacionFilter.setPromptText("Filtrar por capacitación");
        capacitacionFilter.setStyle("-fx-background-color:#0f172a; -fx-text-fill:white;");
        capacitacionFilter.setOnAction(e -> refreshByCapacitacion());

        Button clearFilter = new Button("Limpiar");
        clearFilter.setOnAction(e -> refreshToday());
        clearFilter.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6;");

        HBox filters = new HBox(10, allBtn, activasBtn, cerradasBtn, hoyBtn, capacitacionFilter, clearFilter);
        filters.setAlignment(Pos.CENTER_LEFT);

        table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
        table.getStylesheets().add(getClass().getResource("/css/darkscroll.css").toExternalForm());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No hay asistencias"));

        TableColumn<AttendanceControlModel, String> profCol = new TableColumn<>("Profesional");
        profCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProfesionalNombreCompleto()));

        TableColumn<AttendanceControlModel, String> capCol = new TableColumn<>("Capacitación");
        capCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCapacitacionNombre()));

        TableColumn<AttendanceControlModel, String> fechaCol = new TableColumn<>("Fecha");
        fechaCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFechaEvento().toString()));

        TableColumn<AttendanceControlModel, String> entradaCol = createTimeColumn("Entrada", "hora_entrada", "#4ade80");
        TableColumn<AttendanceControlModel, String> salidaAlmCol = createTimeColumn("Salida Alm.", "hora_salida_almuerzo", "#facc15");
        TableColumn<AttendanceControlModel, String> regresoAlmCol = createTimeColumn("Regreso", "hora_regreso_almuerzo", "#38bdf8");
        TableColumn<AttendanceControlModel, String> salidaFinCol = createTimeColumn("Salida Final", "hora_salida_final", "#f87171");

        TableColumn<AttendanceControlModel, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    AttendanceControlModel model = getTableView().getItems().get(getIndex());
                    boolean cerrado = model.getEstado() == 0;
                    setText(cerrado ? "Cerrado" : "Abierto");
                    setTextFill(cerrado ? Color.web("#f87171") : Color.web("#4ade80"));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<AttendanceControlModel, Void> actionCol = new TableColumn<>("Acciones");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon(FontAwesome.PENCIL);
            {
                editIcon.setIconSize(18);
                editIcon.setIconColor(Color.web("#fde68a"));
                editIcon.setOnMouseClicked(e -> {
                    AttendanceControlModel model = getTableView().getItems().get(getIndex());
                    if (model.getEstado() == 1) {
                        EditAttendanceControlView.show(model, updated -> refreshToday());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editIcon);
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(profCol, capCol, fechaCol, entradaCol, salidaAlmCol, regresoAlmCol, salidaFinCol, estadoCol, actionCol);
        centerColumnContent(profCol);
        centerColumnContent(capCol);
        centerColumnContent(fechaCol);

        VBox.setMargin(table, new Insets(10, 0, 0, 0));
        card.getChildren().addAll(title, filters, table);

        root = new StackPane();
        root.setStyle("-fx-background-color: #0f172a;");
        root.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
        StackPane.setMargin(card, new Insets(40, 0, 40, 0));

        refreshToday();
        loadCapacitaciones();

        return root;
    }

    private TableColumn<AttendanceControlModel, String> createTimeColumn(String title, String campo, String color) {
        TableColumn<AttendanceControlModel, String> col = new TableColumn<>(title);
        col.setCellFactory(column -> new TableCell<>() {
            private final FontIcon icon = new FontIcon();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    AttendanceControlModel model = getTableView().getItems().get(getIndex());
                    LocalTime hora = switch (campo) {
                        case "hora_entrada" -> model.getHoraEntrada();
                        case "hora_salida_almuerzo" -> model.getHoraSalidaAlmuerzo();
                        case "hora_regreso_almuerzo" -> model.getHoraRegresoAlmuerzo();
                        case "hora_salida_final" -> model.getHoraSalidaFinal();
                        default -> null;
                    };

                    if (model.getEstado() == 0 && hora == null) {
                        setText("--");
                        setGraphic(null);
                        return;
                    }

                    if (hora != null) {
                        setText(hora.toString());
                        setGraphic(null);
                    } else if (model.getEstado() == 1) {
                        icon.setIconLiteral(switch (campo) {
                            case "hora_entrada" -> "fa-circle";
                            case "hora_salida_almuerzo" -> "fa-cutlery";
                            case "hora_regreso_almuerzo" -> "fa-sign-in";
                            case "hora_salida_final" -> "fa-sign-out";
                            default -> "fa-circle";
                        });
                        icon.setIconColor(Color.web(color));
                        icon.setIconSize(14);
                        icon.setOnMouseClicked(e -> ConfirmAttendanceControlView.show(
                                title, model.getProfesionalNombreCompleto(), LocalTime.now(), confirmed -> {
                                    if (confirmed) {
                                        try {
                                            controller.updateHoraAsistencia(model.getAsistenciaDetalleId(), campo, LocalTime.now());
                                            refreshToday();
                                        } catch (SQLException ex) {
                                            ToastUtil.showToast(root, "✖ Error al registrar hora", false);
                                        }
                                    }
                                    return null;
                                }
                        ));
                        setGraphic(icon);
                        setText(null);
                    } else {
                        setText("--");
                        setGraphic(null);
                    }
                    setAlignment(Pos.CENTER);
                }
            }
        });
        return col;
    }

    private void refreshAll() {
        try {
            List<AttendanceControlModel> list = controller.getAllAttendances();
            setTableData(list);
        } catch (SQLException e) {
            ToastUtil.showToast(root, "✖ Error al cargar asistencias", false);
        }
    }

    private void refreshByEstado(boolean cerrado) {
        try {
            int estado = cerrado ? 0 : 1;
            List<AttendanceControlModel> list = controller.getAttendancesByStatus(estado);
            setTableData(list);
        } catch (SQLException e) {
            ToastUtil.showToast(root, "✖ Error al cargar", false);
        }
    }

    private void refreshToday() {
        try {
            List<AttendanceControlModel> list = controller.getAttendancesByDate(LocalDate.now());
            setTableData(list);
        } catch (SQLException e) {
            ToastUtil.showToast(root, "✖ Error al cargar asistencias de hoy", false);
        }
    }

    private void refreshByCapacitacion() {
        String selected = capacitacionFilter.getValue();
        if (selected != null && !selected.isBlank()) {
            try {
                List<AttendanceControlModel> list = controller.getAttendancesByCapacitacion(selected);
                setTableData(list);
            } catch (SQLException e) {
                ToastUtil.showToast(root, "✖ Error al filtrar", false);
            }
        }
    }

    private void loadCapacitaciones() {
        try {
            capacitacionFilter.getItems().setAll(controller.getAllCapacitaciones());
        } catch (SQLException e) {
            ToastUtil.showToast(root, "✖ Error al cargar capacitaciones", false);
        }
    }

    private void setTableData(List<AttendanceControlModel> list) {
        data = FXCollections.observableArrayList(list);
        filteredData = new FilteredList<>(data, b -> true);
        table.setItems(filteredData);
        table.setFixedCellSize(40);
        table.prefHeightProperty().bind(
                table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01))
        );
    }

    private void styleFilterButton(Button btn) {
        btn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6;");
    }

    private <T> void centerColumnContent(TableColumn<AttendanceControlModel, T> column) {
        column.setCellFactory(col -> new TableCell<AttendanceControlModel, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER);
            }
        });
    }
}
