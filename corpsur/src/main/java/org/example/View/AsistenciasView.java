package org.example.View;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.example.Controller.AsistenciasController;
import org.example.Model.AsistenciasModel;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AsistenciasView {

    private TableView<AsistenciasModel> table;
    private AsistenciasController controller;
    private DatePicker filterDatePicker;
    private StackPane rootStackPane;

    public StackPane getView() {
        controller = new AsistenciasController();

        // Card principal
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.setMaxWidth(1000);

        // Header con título, botón + y filtro
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Planificar Asistencia");
        title.setFont(Font.font("Segoe UI", 20));
        title.setStyle("-fx-text-fill: white;");

        FontIcon addIcon = new FontIcon(FontAwesome.PLUS);
        addIcon.setIconSize(18);
        addIcon.setIconColor(javafx.scene.paint.Color.WHITE);
        Button addButton = new Button("", addIcon);
        addButton.setStyle("-fx-background-color: #334155; -fx-background-radius: 8;");
        addButton.setOnAction(e -> showAddDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label filterLabel = new Label("Filtrar por fecha:");
        filterLabel.setFont(Font.font("Segoe UI", 15));
        filterLabel.setStyle("-fx-text-fill: white;");

        filterDatePicker = new DatePicker(LocalDate.now());
        filterDatePicker.getStyleClass().add("date-picker");
        filterDatePicker.setStyle(
                "-fx-background-radius: 8; " +
                        "-fx-background-color: #1f2937; " +
                        "-fx-text-fill: white;"
        );
        filterDatePicker.valueProperty().addListener((obs, o, n) -> refreshTable());

        header.getChildren().addAll(title, addButton, spacer, filterLabel, filterDatePicker);

        // Tabla de eventos
        table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setPlaceholder(new Label("No hay eventos para la fecha seleccionada"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AsistenciasModel, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId())
        );
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<AsistenciasModel, String> fechaCol = new TableColumn<>("Fecha");
        fechaCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFecha().toString())
        );
        fechaCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<AsistenciasModel, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTipo())
        );
        tipoCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<AsistenciasModel, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().isEstado() ? "Abierta" : "Cerrada"
                )
        );
        estadoCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<AsistenciasModel, Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final HBox box = new HBox(10);
            private final Button closeBtn = new Button("Cerrar");
            private final Button reopenBtn = new Button("Reabrir");
            {
                closeBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 8;");
                reopenBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 8;");
                closeBtn.setOnAction(e -> handleClose());
                reopenBtn.setOnAction(e -> handleReopen());
                box.setAlignment(Pos.CENTER);
            }
            private void handleClose() {
                AsistenciasModel m = getTableView().getItems().get(getIndex());
                try {
                    if (controller.cerrarEvento(m.getId()))
                        ToastUtil.showToastTopRight(rootStackPane, "✔ Evento cerrado", true);
                    refreshTable();
                } catch (SQLException ex) {
                    ToastUtil.showToastTopRight(rootStackPane, "✖ " + ex.getMessage(), false);
                }
            }
            private void handleReopen() {
                AsistenciasModel m = getTableView().getItems().get(getIndex());
                try {
                    if (controller.reabrirEvento(m.getId()))
                        ToastUtil.showToastTopRight(rootStackPane, "✔ Evento reabierto", true);
                    refreshTable();
                } catch (SQLException ex) {
                    ToastUtil.showToastTopRight(rootStackPane, "✖ " + ex.getMessage(), false);
                }
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    AsistenciasModel m = getTableView().getItems().get(getIndex());
                    setGraphic(m.isEstado() ? closeBtn : reopenBtn);
                }
            }
        });

        table.getColumns().addAll(idCol, fechaCol, tipoCol, estadoCol, actionsCol);
        refreshTable();

        // Montaje de la vista
        card.getChildren().addAll(header, table);

        rootStackPane = new StackPane(card);
        rootStackPane.setStyle("-fx-background-color: #0f172a;");
        StackPane.setMargin(card, new Insets(40, 0, 40, 0));

        // Carga de estilos
        String theme = getClass().getResource("/css/dark-theme.css").toExternalForm();
        String scroll = getClass().getResource("/css/darkscroll.css").toExternalForm();
        String dpDarkCss   = getClass().getResource("/css/datepicker-dark.css").toExternalForm();

        rootStackPane.getStylesheets().addAll(theme, scroll, dpDarkCss);

        return rootStackPane;
    }

    private void showAddDialog() {
        AddAsistenciasView.show(success -> {
            if (success) {
                ToastUtil.showToastTopRight(rootStackPane, "✔ Eventos generados", true);
                refreshTable();
            }
            return null;
        });
    }

    private void refreshTable() {
        try {
            LocalDate filtro = filterDatePicker.getValue();
            List<AsistenciasModel> events = controller.getByDate(filtro);
            ObservableList<AsistenciasModel> data = FXCollections.observableArrayList(events);
            table.setItems(data);
            table.setFixedCellSize(40);
            table.prefHeightProperty().bind(
                    table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01))
            );
        } catch (SQLException e) {
            ToastUtil.showToast(rootStackPane, "✖ Error al cargar eventos", false);
        }
    }
}
