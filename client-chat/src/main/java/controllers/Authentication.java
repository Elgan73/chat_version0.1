package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Authentication {
    public Button join;
    public Button registration;
    private double xOffset = 0;
    private double yOffset = 0;

    public void join(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            join.getScene().getWindow().hide();
        });
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/authorized.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();
    }

    public void registration(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            registration.getScene().getWindow().hide();
        });
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/signUp.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();
    }

    public void closeWindow(MouseEvent mouseEvent) {
        Platform.exit();
        System.exit(0);
    }
}
