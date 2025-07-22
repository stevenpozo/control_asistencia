package org.example.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;

public class DashboardView {

    private final String username;
    private StackPane content; // <- Área central modificable

    public DashboardView(String username) {
        this.username = username;
    }

    public void show(Stage stage) {
        VBox sidebar = new VBox(25);
        sidebar.setStyle("-fx-background-color: #111827;");
        sidebar.setPadding(new Insets(30, 10, 20, 10));
        sidebar.setPrefWidth(220);

        VBox userSection = new VBox(8);
        userSection.setAlignment(Pos.CENTER);

        FontIcon profileIcon = new FontIcon(FontAwesome.USER_CIRCLE);
        profileIcon.setIconSize(50);
        profileIcon.setIconColor(Color.WHITE);

        Label nameLabel = new Label("Welcome " + username);
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("Segoe UI", 13));

        Label roleLabel = new Label("Corpsur Admin");
        roleLabel.setTextFill(Color.GRAY);
        roleLabel.setFont(Font.font("Segoe UI", 11));

        userSection.getChildren().addAll(profileIcon, nameLabel, roleLabel);

        VBox menu = new VBox(10);
        menu.setPadding(new Insets(30, 0, 30, 20));

        menu.getChildren().addAll(
                createSidebarButton("Dashboard", FontAwesome.HOME, () -> content.getChildren().setAll(new Label("Welcome to Dashboard!"))),
                createSidebarButton("Profesionales", FontAwesome.USER_PLUS, () -> content.getChildren().setAll(new ProfessionalView().getView())),
                createSidebarButton("Laboratorios", FontAwesome.FLASK, () -> content.getChildren().setAll(new LaboratoryView().getView())),
                createSidebarButton("Profesiones", FontAwesome.GRADUATION_CAP, () -> content.getChildren().setAll(new ProfessionsView().getView())),
                createSidebarButton("Capacitaciones", FontAwesome.BOOK, () -> content.getChildren().setAll(new TrainingView().getView())),
                createSidebarButton("Planificar Asistencia", FontAwesome.CALENDAR_CHECK_O, () -> content.getChildren().setAll(new AsistenciasView().getView())),
                createSidebarButton("Reports", FontAwesome.FILE_TEXT_O, () -> content.getChildren().setAll(new Label("Reports"))),
                createSidebarButton("Settings", FontAwesome.COG, () -> content.getChildren().setAll(new Label("Settings")))
        );

        FontIcon logoutIcon = new FontIcon(FontAwesome.SIGN_OUT);
        logoutIcon.setIconSize(14);
        logoutIcon.setIconColor(Color.WHITE);

        Label logoutLabel = new Label("Logout");
        logoutLabel.setTextFill(Color.WHITE);
        logoutLabel.setFont(Font.font("Segoe UI", 13));

        HBox logoutButton = new HBox(10, logoutIcon, logoutLabel);
        logoutButton.setPadding(new Insets(10, 0, 10, 20));
        logoutButton.setAlignment(Pos.CENTER_LEFT);
        logoutButton.setStyle("-fx-background-color: transparent;");
        logoutButton.setOnMouseClicked((MouseEvent e) -> {
            stage.close();
            Stage loginStage = new Stage();
            new LoginView().show(loginStage);
        });

        VBox.setVgrow(menu, Priority.ALWAYS);
        sidebar.getChildren().addAll(userSection, menu, logoutButton);

        // Central content area
        content = new StackPane();
        content.setStyle("-fx-background-color: #0f172a;");
        content.getChildren().add(new Label("Welcome to Dashboard!"));

        BorderPane layout = new BorderPane();
        layout.setLeft(sidebar);
        layout.setCenter(content);

        // Obtener dimensiones de la pantalla
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();

        Scene scene = new Scene(layout, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.setTitle("Dashboard - Corpsur");
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();

    }


    private HBox createSidebarButton(String text, FontAwesome icon, Runnable action) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(14);
        fontIcon.setIconColor(Color.LIGHTGRAY);

        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Segoe UI", 13));

        HBox button = new HBox(10, fontIcon, label);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(8, 10, 8, 10));
        button.setStyle("-fx-background-color: transparent;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #1f2937; -fx-background-radius: 8;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent;"));
        button.setOnMouseClicked(e -> action.run());

        return button;
    }
}
