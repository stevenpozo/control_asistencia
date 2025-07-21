package org.example.Utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ToastUtil {

    public static void showToast(StackPane root, String text, boolean success) {
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

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> root.getChildren().remove(toast));

        new SequentialTransition(fadeIn, pause, fadeOut).play();
    }

    public static void showToastTopRight(StackPane root, String text, boolean success) {
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

        StackPane.setAlignment(toast, Pos.TOP_RIGHT);
        StackPane.setMargin(toast, new Insets(30, 30, 0, 0)); // top, right, bottom, left

        root.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> root.getChildren().remove(toast));

        new SequentialTransition(fadeIn, pause, fadeOut).play();
    }

}
