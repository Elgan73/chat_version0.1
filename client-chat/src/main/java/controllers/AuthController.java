package controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    public Button enterChat;
    public TextField userLogin;
    public TextField userPassword;
    public Button registration;
    private static Network net = Network.getINSTANCE();
    private DataInputStream in;
    private DataOutputStream out;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registration.setOnAction(event -> {
            registration.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/signUp.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });

        try {
            net.connect("localhost", 8189);
            in = net.getInputStream();
            out = net.getOutputStream();
            enterChat.setOnAction(actionEvent -> {
                String login = userLogin.getText();
                String pass = userPassword.getText();
                String msg = "/lp," + login + "," + pass;
                try {
                    out.writeUTF(msg);
                    out.flush();
                    if (in.readUTF().equals("/authOk")) {
                        Platform.runLater(() -> {
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource("/ch.fxml"));
                            try {
                                loader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Parent root = loader.getRoot();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.showAndWait();
                        });

                    } else {
                        System.out.println("smth wrong!");
//                        FXMLLoader loader = new FXMLLoader();
//                        loader.setLocation(getClass().getResource("/signUp.fxml"));
//                        try {
//                            loader.load();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        Parent root = loader.getRoot();
//                        Stage stage = new Stage();
//                        stage.setScene(new Scene(root));
//                        stage.showAndWait();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}


