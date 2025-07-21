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
import org.example.Controller.LaboratoryController;
import org.example.Model.LaboratoryModel;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.List;

public class LaboratoryView {

    private TableView<LaboratoryModel> table;
    private LaboratoryController controller;
    private TextField searchField;
    private StackPane rootStackPane;
    private boolean mostrandoActivos = true;
    private ObservableList<LaboratoryModel> data;
    private FilteredList<LaboratoryModel> filteredData;

    public StackPane getView() {
        controller = new LaboratoryController();

        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.setMaxWidth(1000);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Agregar Laboratorio");
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
        searchField.setPromptText("Buscar laboratorio...");
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

        // Botones de filtro
        Button showActiveBtn = new Button("Laboratorios habilitados");
        showActiveBtn.setStyle("-fx-background-color: #bbf7d0; -fx-text-fill: black; -fx-background-radius: 6;");

        Button showInactiveBtn = new Button("Laboratorios inhabilitados");
        showInactiveBtn.setStyle("-fx-background-color: #fecaca; -fx-text-fill: black; -fx-background-radius: 6;");

        showActiveBtn.setOnAction(e -> {
            mostrandoActivos = true;
            refreshTable(true);
        });

        showInactiveBtn.setOnAction(e -> {
            mostrandoActivos = false;
            refreshTable(false);
        });

        HBox filterButtons = new HBox(15, showActiveBtn, showInactiveBtn);
        filterButtons.setAlignment(Pos.CENTER_LEFT);

        // Tabla
        table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setPlaceholder(new Label("No hay laboratorios registrados"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LaboratoryModel, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<LaboratoryModel, Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(10);
            private final FontIcon editIcon = new FontIcon(FontAwesome.PENCIL);
            private final FontIcon switchIcon = new FontIcon();

            {
                editIcon.setIconSize(20);
                editIcon.setIconColor(Color.web("#FDE68A")); // amarillo pastel
                editIcon.setOnMouseClicked(e -> {
                    LaboratoryModel lab = getTableView().getItems().get(getIndex());
                    showEditDialog(lab);
                });

                switchIcon.setIconSize(22);
                switchIcon.setOnMouseClicked(e -> {
                    LaboratoryModel lab = getTableView().getItems().get(getIndex());
                    showStatusConfirmation(lab);
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
                    LaboratoryModel lab = getTableView().getItems().get(getIndex());
                    if (lab.isStatus()) {
                        switchIcon.setIconLiteral("fa-toggle-on");
                        switchIcon.setIconColor(Color.web("#4ade80")); // verde
                    } else {
                        switchIcon.setIconLiteral("fa-toggle-off");
                        switchIcon.setIconColor(Color.web("#f87171")); // rojo
                    }
                    setGraphic(actionBox);
                }
            }
        });

        table.getColumns().addAll(nameCol, actionsCol);
        centerColumnContent(nameCol);
        // Cargar por defecto los habilitados
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

    private void refreshTable(boolean showActivos) {
        try {
            List<LaboratoryModel> labs = showActivos
                    ? controller.getActiveLaboratories()
                    : controller.getInactiveLaboratories();

            data = FXCollections.observableArrayList(labs);
            filteredData = new FilteredList<>(data, b -> true);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(lab -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    return lab.getName().toLowerCase().contains(newVal.toLowerCase());
                });
            });

            table.setItems(filteredData);
            table.setFixedCellSize(40);
            table.prefHeightProperty().bind(
                    table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01))
            );
        } catch (SQLException e) {
            ToastUtil.showToast(rootStackPane, "✖ Error al cargar laboratorios", false);
        }
    }

    private void showAddDialog() {
        AddLaboratoryView.show(name -> refreshTable(mostrandoActivos));
    }

    private void showEditDialog(LaboratoryModel lab) {
        EditLaboratoryView.show(lab, () -> refreshTable(mostrandoActivos));
    }

    private void showStatusConfirmation(LaboratoryModel lab) {
        boolean newStatus = !lab.isStatus();
        StatusLaboratoryView.show(lab.getName(), newStatus, confirmed -> {
            if (confirmed) {
                try {
                    boolean updated;
                    if (newStatus) {
                        updated = controller.activateLaboratoryStatus(lab.getId());
                    } else {
                        updated = controller.inactivateLaboratoryStatus(lab.getId());
                    }

                    if (updated) {
                        ToastUtil.showToastTopRight(
                                rootStackPane,
                                newStatus ? "✔ Laboratorio activado" : "✔ Laboratorio inhabilitado",
                                true
                        );
                        refreshTable(mostrandoActivos); // 👈 aquí se mantiene la vista actual
                    }
                } catch (SQLException e) {
                    ToastUtil.showToastTopRight(rootStackPane, "✖ " + e.getMessage(), false);
                }
            }
            return null;
        });
    }

    private <T> void centerColumnContent(TableColumn<LaboratoryModel, T> column) {
        column.setCellFactory(col -> {
            TableCell<LaboratoryModel, T> cell = new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                    setAlignment(Pos.CENTER); // centra el contenido
                }
            };
            return cell;
        });
    }
}
