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
import org.example.Controller.ProfessionalController;
import org.example.Model.ProfessionalModel;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.List;

public class ProfessionalView {

    private TableView<ProfessionalModel> table;
    private ProfessionalController controller;
    private TextField searchField;
    private StackPane rootStackPane;
    private boolean mostrandoActivos = true;
    private ObservableList<ProfessionalModel> data;
    private FilteredList<ProfessionalModel> filteredData;

    public StackPane getView() {
        controller = new ProfessionalController();

        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.setMaxWidth(1200);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Gestión de Profesionales");
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
        searchField.setPromptText("Buscar profesional...");
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
        Button showActiveBtn = new Button("Profesionales habilitados");
        showActiveBtn.setStyle("-fx-background-color: #bbf7d0; -fx-text-fill: black; -fx-background-radius: 6;");
        showActiveBtn.setOnAction(e -> {
            mostrandoActivos = true;
            refreshTable(true);
        });

        Button showInactiveBtn = new Button("Profesionales inhabilitados");
        showInactiveBtn.setStyle("-fx-background-color: #fecaca; -fx-text-fill: black; -fx-background-radius: 6;");
        showInactiveBtn.setOnAction(e -> {
            mostrandoActivos = false;
            refreshTable(false);
        });

        HBox filterButtons = new HBox(15, showActiveBtn, showInactiveBtn);
        filterButtons.setAlignment(Pos.CENTER_LEFT);

        table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setPlaceholder(new Label("No hay profesionales registrados"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ProfessionalModel, String> nameCol = new TableColumn<>("Nombre Completo");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNombre() + " " + data.getValue().getApellido()));

        TableColumn<ProfessionalModel, String> cedulaCol = new TableColumn<>("Cédula");
        cedulaCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCedula()));

        TableColumn<ProfessionalModel, String> telefonoCol = new TableColumn<>("Teléfono");
        telefonoCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefono()));

        TableColumn<ProfessionalModel, String> nacimientoCol = new TableColumn<>("Nacimiento");
        nacimientoCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFechaNacimiento()));

        TableColumn<ProfessionalModel, String> ubicacionCol = new TableColumn<>("Ubicación");
        ubicacionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getProvinciaNombre() + " / " + data.getValue().getCiudadNombre()));

        TableColumn<ProfessionalModel, String> profesionCol = new TableColumn<>("Profesión");
        profesionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProfesionNombre()));

        TableColumn<ProfessionalModel, Void> accionesCol = new TableColumn<>("Acciones");
        accionesCol.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(10);
            private final FontIcon editIcon = new FontIcon(FontAwesome.PENCIL);
            private final FontIcon switchIcon = new FontIcon();

            {
                editIcon.setIconSize(20);
                editIcon.setIconColor(Color.web("#FDE68A"));
                editIcon.setOnMouseClicked(e -> {
                    ProfessionalModel prof = getTableView().getItems().get(getIndex());
                    showEditDialog(prof);
                });

                switchIcon.setIconSize(22);
                switchIcon.setOnMouseClicked(e -> {
                    ProfessionalModel prof = getTableView().getItems().get(getIndex());
                    showStatusConfirmation(prof);
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
                    ProfessionalModel prof = getTableView().getItems().get(getIndex());
                    if (prof.isActivo()) {
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

        table.getColumns().addAll(nameCol, cedulaCol, telefonoCol, nacimientoCol, ubicacionCol, profesionCol, accionesCol);
        centerColumnContent(nameCol);
        centerColumnContent(cedulaCol);
        centerColumnContent(telefonoCol);
        centerColumnContent(nacimientoCol);
        centerColumnContent(ubicacionCol);
        centerColumnContent(profesionCol);

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

        refreshTable(true);

        return rootStackPane;
    }

    private void refreshTable(boolean activos) {
        try {
            List<ProfessionalModel> list = activos
                    ? controller.getActiveProfessionals()
                    : controller.getInactiveProfessionals();

            data = FXCollections.observableArrayList(list);
            filteredData = new FilteredList<>(data, b -> true);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(p -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    return p.getNombre().toLowerCase().contains(newVal.toLowerCase()) ||
                            p.getCedula().toLowerCase().contains(newVal.toLowerCase()) ||
                            p.getProfesionNombre().toLowerCase().contains(newVal.toLowerCase());
                });
            });

            table.setItems(filteredData);
            table.setFixedCellSize(40);
            table.prefHeightProperty().bind(
                    table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01))
            );
        } catch (SQLException e) {
            ToastUtil.showToastTopRight(rootStackPane, "\u2716 Error al cargar profesionales", false);
        }
    }

    private void showAddDialog() {
        AddProfessionalView.show(prof -> refreshTable(mostrandoActivos));
    }

    private void showEditDialog(ProfessionalModel prof) {
        EditProfesionalView.show(prof, updated -> refreshTable(mostrandoActivos));
    }

    private void showStatusConfirmation(ProfessionalModel prof) {
        boolean newStatus = !prof.isActivo();
        StatusProfessionalView.show(prof.getNombre() + " " + prof.getApellido(), newStatus, confirmed -> {
            if (confirmed) {
                try {
                    boolean updated;
                    if (newStatus) {
                        updated = controller.activateProfessionalStatus(prof.getId());
                    } else {
                        updated = controller.inactivateProfessionalStatus(prof.getId());
                    }

                    if (updated) {
                        ToastUtil.showToastTopRight(
                                rootStackPane,
                                newStatus ? "\u2714 Profesional activado" : "\u2714 Profesional inhabilitado",
                                true
                        );
                        refreshTable(mostrandoActivos);
                    }
                } catch (SQLException e) {
                    ToastUtil.showToastTopRight(rootStackPane, "\u2716 " + e.getMessage(), false);
                }
            }
            return null;
        });
    }

    private <T> void centerColumnContent(TableColumn<ProfessionalModel, T> column) {
        column.setCellFactory(col -> {
            TableCell<ProfessionalModel, T> cell = new TableCell<>() {
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
