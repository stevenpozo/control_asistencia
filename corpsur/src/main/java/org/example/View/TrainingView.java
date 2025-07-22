package org.example.View;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.Controller.TrainingController;
import org.example.Model.TrainingModel;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.List;

public class TrainingView {

    private TableView<TrainingModel> table;
    private TrainingController controller;
    private TextField searchField;
    private StackPane rootStackPane;
    private boolean mostrandoActivos = true;
    private ObservableList<TrainingModel> data;
    private FilteredList<TrainingModel> filteredData;

    public StackPane getView() {
        controller = new TrainingController();

        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.setMaxWidth(1100);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Gestión de Capacitaciones");
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

        FontIcon refreshIcon = new FontIcon(FontAwesome.REFRESH);
        refreshIcon.setIconSize(14);
        Button refreshButton = new Button("", refreshIcon);
        refreshButton.setStyle("-fx-background-color: #334155; -fx-background-radius: 8;");
        refreshButton.setOnAction(e -> refreshTable(mostrandoActivos));

        searchField = new TextField();
        searchField.setPromptText("Buscar capacitación...");
        searchField.setStyle("-fx-background-radius: 8; -fx-background-color: #334155; -fx-text-fill: white;");
        searchField.setPrefWidth(200);

        FontIcon searchIcon = new FontIcon(FontAwesome.SEARCH);
        searchIcon.setIconSize(14);
        searchIcon.setIconColor(Color.WHITE);

        HBox searchBox = new HBox(8, searchIcon, searchField);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        VBox rightControls = new VBox(10);
        rightControls.setAlignment(Pos.TOP_RIGHT);
        rightControls.getChildren().addAll(refreshButton, searchBox);

        header.getChildren().addAll(title, addButton, spacer, rightControls);

        // Botones filtro
        Button showActiveBtn = new Button("Capacitaciones habilitadas");
        showActiveBtn.setStyle("-fx-background-color: #bbf7d0; -fx-text-fill: black; -fx-background-radius: 6;");
        showActiveBtn.setOnAction(e -> {
            mostrandoActivos = true;
            refreshTable(true);
        });

        Button showInactiveBtn = new Button("Capacitaciones inhabilitadas");
        showInactiveBtn.setStyle("-fx-background-color: #fecaca; -fx-text-fill: black; -fx-background-radius: 6;");
        showInactiveBtn.setOnAction(e -> {
            mostrandoActivos = false;
            refreshTable(false);
        });

        HBox filterButtons = new HBox(15, showActiveBtn, showInactiveBtn);
        filterButtons.setAlignment(Pos.CENTER_LEFT);

        // Tabla
        table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setPlaceholder(new Label("No hay capacitaciones registradas"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TrainingModel, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        TableColumn<TrainingModel, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCodigo()));

        TableColumn<TrainingModel, String> colInicio = new TableColumn<>("Inicio");
        colInicio.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFechaInicio()));

        TableColumn<TrainingModel, String> colFin = new TableColumn<>("Fin");
        colFin.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFechaFin()));

        TableColumn<TrainingModel, String> colLab = new TableColumn<>("Laboratorio");
        colLab.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLaboratorioNombre()));

        TableColumn<TrainingModel, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(10);
            private final FontIcon editIcon = new FontIcon(FontAwesome.PENCIL);
            private final FontIcon switchIcon = new FontIcon();

            {
                editIcon.setIconSize(20);
                editIcon.setIconColor(Color.web("#FDE68A"));
                editIcon.setOnMouseClicked(e -> {
                    TrainingModel training = getTableView().getItems().get(getIndex());
                    showEditDialog(training);
                });

                switchIcon.setIconSize(22);
                switchIcon.setOnMouseClicked(e -> {
                    TrainingModel training = getTableView().getItems().get(getIndex());
                    showStatusConfirmation(training);
                });

                actionBox.getChildren().addAll(editIcon, switchIcon);
                actionBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TrainingModel training = getTableView().getItems().get(getIndex());
                    if (training.isActivo()) {
                        switchIcon.setIconLiteral("fa-toggle-on");
                        switchIcon.setIconColor(Color.web("#4ade80"));
                    } else {
                        switchIcon.setIconLiteral("fa-toggle-off");
                        switchIcon.setIconColor(Color.web("#f87171"));
                    }
                    setGraphic(actionBox);
                }
            }
        });

        table.getColumns().addAll(colNombre, colCodigo, colInicio, colFin, colLab, colAcciones);
        centerColumnContent(colNombre);
        centerColumnContent(colCodigo);
        centerColumnContent(colInicio);
        centerColumnContent(colFin);
        centerColumnContent(colLab);

        refreshTable(true);

        VBox.setMargin(table, new Insets(10, 0, 0, 0));
        card.getChildren().addAll(header, filterButtons, table);

        rootStackPane = new StackPane();
        rootStackPane.setStyle("-fx-background-color: #0f172a;");
        rootStackPane.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
        StackPane.setMargin(card, new Insets(40, 0, 40, 0));

        String theme = getClass().getResource("/css/dark-theme.css").toExternalForm();
        String scroll = getClass().getResource("/css/darkscroll.css").toExternalForm();
        rootStackPane.getStylesheets().addAll(theme, scroll);

        return rootStackPane;
    }

    private void refreshTable(boolean activos) {
        try {
            List<TrainingModel> trainings = activos
                    ? controller.getActiveTrainings()
                    : controller.getInactiveTrainings();

            data = FXCollections.observableArrayList(trainings);
            filteredData = new FilteredList<>(data, b -> true);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(t -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    return t.getNombre().toLowerCase().contains(newVal.toLowerCase()) ||
                            t.getCodigo().toLowerCase().contains(newVal.toLowerCase()) ||
                            t.getLaboratorioNombre().toLowerCase().contains(newVal.toLowerCase());
                });
            });

            table.setItems(filteredData);
            table.setFixedCellSize(40);
            table.prefHeightProperty().bind(
                    table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01))
            );
        } catch (SQLException e) {
            ToastUtil.showToast(rootStackPane, "✖ Error al cargar capacitaciones", false);
        }
    }

    private void showAddDialog() {
        AddTrainingView.show(training -> refreshTable(mostrandoActivos));
    }

    private void showEditDialog(TrainingModel training) {
        EditTrainingView.show(training, () -> refreshTable(mostrandoActivos));
    }

    private void showStatusConfirmation(TrainingModel training) {
        boolean newStatus = !training.isActivo();
        StatusTrainingView.show(training.getNombre(), newStatus, confirmed -> {
            if (confirmed) {
                try {
                    boolean updated = newStatus
                            ? controller.activateTraining(training.getId())
                            : controller.inactivateTraining(training.getId());

                    if (updated) {
                        ToastUtil.showToastTopRight(
                                rootStackPane,
                                newStatus ? "✔ Capacitación activada" : "✔ Capacitación inhabilitada",
                                true
                        );
                        refreshTable(mostrandoActivos);
                    }
                } catch (SQLException e) {
                    ToastUtil.showToastTopRight(rootStackPane, "✖ " + e.getMessage(), false);
                }
            }
            return null;
        });
    }

    private <T> void centerColumnContent(TableColumn<TrainingModel, T> column) {
        column.setCellFactory(col -> {
            TableCell<TrainingModel, T> cell = new TableCell<>() {
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
            };
            return cell;
        });
    }
}
