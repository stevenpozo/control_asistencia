// ProfessionalTrainingView actualizado

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
import org.example.Controller.ProfessionalTrainingController;
import org.example.Utils.ToastUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ProfessionalTrainingView {

    private TableView<String[]> table;
    private TextField searchField;
    private StackPane rootStackPane;
    private ObservableList<String[]> data;
    private FilteredList<String[]> filteredData;

    public StackPane getView() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.setMaxWidth(1050);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Inscripción");
        title.setFont(Font.font("Segoe UI", 17));
        title.setStyle("-fx-text-fill: white;");

        FontIcon addIcon = new FontIcon(FontAwesome.PLUS);
        addIcon.setIconSize(16);
        addIcon.setIconColor(Color.WHITE);
        Button addButton = new Button("", addIcon);
        addButton.setStyle("-fx-background-color: #334155; -fx-background-radius: 8;");
        addButton.setOnAction(e -> {
            AddProfessionalTrainingView.show();
            refreshTable();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon refreshIcon = new FontIcon(FontAwesome.REFRESH);
        refreshIcon.setIconSize(14);
        Button refreshButton = new Button("", refreshIcon);
        refreshButton.setStyle("-fx-background-color: #334155; -fx-background-radius: 8;");
        refreshButton.setOnAction(e -> refreshTable());

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

        // Tabla
        table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setPlaceholder(new Label("No hay capacitaciones registradas"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] headers = {"Código", "Nombre", "Inicio", "Fin", "Laboratorio", "Doctores"};
        for (int i = 0; i < headers.length; i++) {
            final int index = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[index]));
            col.setStyle("-fx-alignment: CENTER;");
            table.getColumns().add(col);
        }

        TableColumn<String[], Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final FontIcon viewIcon = new FontIcon(FontAwesome.USER_MD);
            {
                viewIcon.setIconSize(18);
                viewIcon.setIconColor(Color.web("#38bdf8"));
                viewIcon.setOnMouseClicked(e -> {
                    String[] row = getTableView().getItems().get(getIndex());
                    try {
                        ProfessionalTrainingController controller = new ProfessionalTrainingController();
                        String[] curso = controller.findTrainingByCode(row[0]);
                        if (curso != null) {
                            int id = Integer.parseInt(curso[0]);
                            Date inicio = Date.valueOf(row[2]);
                            Date fin = Date.valueOf(row[3]);
                            ProfessionalTrainingControlView.show(id, row[1], inicio, fin);
                        }
                    } catch (Exception ex) {
                        ToastUtil.showToast(rootStackPane, "✖ Error al abrir control", false);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewIcon);
                setAlignment(Pos.CENTER);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        });

        table.getColumns().add(actionsCol);

        refreshTable();

        VBox.setMargin(table, new Insets(10, 0, 0, 0));
        card.getChildren().addAll(header, table);

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

    private void refreshTable() {
        try {
            ProfessionalTrainingController controller = new ProfessionalTrainingController();
            List<String[]> cursos = controller.getAllActiveTrainingsWithCount();

            data = FXCollections.observableArrayList(cursos);
            filteredData = new FilteredList<>(data, b -> true);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(row -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String filter = newVal.toLowerCase();
                    return row[0].toLowerCase().contains(filter) || row[1].toLowerCase().contains(filter);
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
}
