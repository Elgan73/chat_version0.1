package controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
            Socket socket = new Socket("localhost", 8189);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            enterChat.setOnAction(actionEvent -> {
                String login = userLogin.getText();
                String pass = userPassword.getText();
                String msg = "/?" + login + ":" + pass;
                try {
                    out.writeUTF(msg);
                    out.flush();
                    if (in.readUTF().equals("/authOk")) {
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
                    } else {
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


