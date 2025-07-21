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
import org.example.Controller.ProfessionsController;
import org.example.Model.ProfessionalModel;
import org.example.Model.ProfessionsModel;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.List;

public class ProfessionsView {

    private TableView<ProfessionsModel> table;
    private ProfessionsController controller;
    private TextField searchField;
    private StackPane rootStackPane;
    private boolean mostrandoActivos = true;
    private ObservableList<ProfessionsModel> data;
    private FilteredList<ProfessionsModel> filteredData;

    public StackPane getView() {
        controller = new ProfessionsController();

        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.setMaxWidth(1000);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Agregar Profesión");
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
        searchField.setPromptText("Buscar profesión...");
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
        Button showActiveBtn = new Button("Profesiones habilitadas");
        showActiveBtn.setStyle("-fx-background-color: #bbf7d0; -fx-text-fill: black; -fx-background-radius: 6;");

        Button showInactiveBtn = new Button("Profesiones inhabilitadas");
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

        table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setPlaceholder(new Label("No hay profesiones registradas"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ProfessionsModel, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<ProfessionsModel, Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(10);
            private final FontIcon editIcon = new FontIcon(FontAwesome.PENCIL);
            private final FontIcon switchIcon = new FontIcon();

            {
                editIcon.setIconSize(20);
                editIcon.setIconColor(Color.web("#FDE68A"));
                editIcon.setOnMouseClicked(e -> {
                    ProfessionsModel p = getTableView().getItems().get(getIndex());
                    showEditDialog(p);
                });

                switchIcon.setIconSize(22);
                switchIcon.setOnMouseClicked(e -> {
                    ProfessionsModel p = getTableView().getItems().get(getIndex());
                    showStatusConfirmation(p);
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
                    ProfessionsModel p = getTableView().getItems().get(getIndex());
                    if (p.isStatus()) {
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

        table.getColumns().addAll(nameCol, actionsCol);
        centerColumnContent(nameCol);

        refreshTable(true); // Mostrar activos al inicio

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
            List<ProfessionsModel> list = showActivos
                    ? controller.getActiveProfessions()
                    : controller.getInactiveProfessions();

            data = FXCollections.observableArrayList(list);
            filteredData = new FilteredList<>(data, b -> true);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(p -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    return p.getName().toLowerCase().contains(newVal.toLowerCase());
                });
            });

            table.setItems(filteredData);
            table.setFixedCellSize(40);
            table.prefHeightProperty().bind(
                    table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01))
            );

        } catch (SQLException e) {
            ToastUtil.showToastTopRight(rootStackPane, "✖ Error al cargar profesiones", false);
        }
    }

    private void showAddDialog() {
        AddProfessionView.show(name -> refreshTable(mostrandoActivos));
    }

    private void showEditDialog(ProfessionsModel p) {
        EditProfessionView.show(p, () -> refreshTable(mostrandoActivos));
    }

    private void showStatusConfirmation(ProfessionsModel p) {
        boolean newStatus = !p.isStatus();
        StatusProfessionsView.show(p.getName(), newStatus, confirmed -> {
            if (confirmed) {
                try {
                    boolean updated;
                    if (newStatus) {
                        updated = controller.activateProfession(p.getId());
                    } else {
                        updated = controller.inactivateProfession(p.getId());
                    }

                    if (updated) {
                        ToastUtil.showToastTopRight(rootStackPane,
                                newStatus ? "✔ Profesión activada" : "✔ Profesión inhabilitada", true);
                        refreshTable(mostrandoActivos); // Mantener vista actual
                    }
                } catch (SQLException e) {
                    ToastUtil.showToastTopRight(rootStackPane, "✖ " + e.getMessage(), false);
                }
            }
            return null;
        });
    }

    private <T> void centerColumnContent(TableColumn<ProfessionsModel, T> column) {
        column.setCellFactory(col -> {
            TableCell<ProfessionsModel, T> cell = new TableCell<>() {
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
