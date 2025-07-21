package org.example.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.Controller.LoginController;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;

public class LoginView {

    public void show(Stage stage) {
        // Avatar icon
        FontIcon avatarIcon = new FontIcon(FontAwesome.USER_CIRCLE);
        avatarIcon.setIconSize(60);
        avatarIcon.setIconColor(Color.WHITE);

        Label title = new Label("CORPSUR");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Welcome back again");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        subtitle.setTextFill(Color.GRAY);

        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleDarkInput(usernameField);

        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleDarkInput(passwordField);

        // Login button
        Button loginButton = new Button("LOGIN");
        loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #3b82f6, #2563eb);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 0;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0.2, 0, 2);"
        );
        loginButton.setPrefWidth(250);

        // Toast message
        Label message = new Label();
        message.setTextFill(Color.WHITE);

        VBox card = new VBox(12,
                avatarIcon,
                title,
                subtitle,
                usernameField,
                passwordField,
                loginButton,
                message
        );
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(300);
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.04);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: rgba(255,255,255,0.08);"
        );

        // Action
        StackPane root = new StackPane();
        root.getChildren().add(card);
        root.setStyle("-fx-background-color: #111827;");
        root.setPadding(new Insets(40));

        //Action button Login
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            boolean valid = new LoginController().authenticate(username, passwordField.getText());
            showToast(root, valid ? "✔ Login Successful" : "✖ Invalid Credentials", valid);

            if (valid) {
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.5));
                pause.setOnFinished(ev -> {
                    stage.close();

                    Stage dashboardStage = new Stage();
                    new DashboardView(username).show(dashboardStage); // <-- lo pasamos aquí
                });
                pause.play();
            }
        });



        Scene scene = new Scene(root, 400, 500);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Login - Corpsur");
        stage.show();
    }

    private void styleDarkInput(TextField field) {
        field.setStyle(
                "-fx-background-color: #1f2937;" +
                        "-fx-background-radius: 6;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #9ca3af;" +
                        "-fx-font-size: 13px;" +
                        "-fx-border-color: transparent;"
        );
        field.setPrefWidth(250);
    }

    private void showToast(StackPane root, String text, boolean success) {
        Label toast = new Label(text);
        toast.setStyle(
                "-fx-background-color: " + (success ? "#10b981" : "#ef4444") + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-font-size: 14px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.2, 0, 2);"
        );

        toast.setOpacity(0);
        StackPane.setAlignment(toast, Pos.TOP_CENTER);
        root.getChildren().add(toast);

        var fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.5), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        var pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));

        var fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.5), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> root.getChildren().remove(toast));

        new javafx.animation.SequentialTransition(fadeIn, pause, fadeOut).play();
    }
}