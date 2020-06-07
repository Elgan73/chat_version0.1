package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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

    private void openRegistrationWindow() {
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

    private void openChatWindow() {
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
    }

    private void sendRequest() {
        try {
            out.writeUTF("/lp," + userLogin.getText() + "," + userPassword.getText());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        net.connect("localhost", 8189);
        in = net.getInputStream();
        out = net.getOutputStream();

        Thread t = new Thread(() -> {
            String msg;
            while (true) {
                try {
                    msg = in.readUTF();
                    System.out.println(msg);
                    if (msg.startsWith("/authOk")) {
                        Platform.runLater(this::openChatWindow);
                        Platform.runLater(() -> {
                            enterChat.getScene().getWindow().hide();
                        });
                    }
//                    else {
//                        System.out.println("smth wrong with authority!");
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void clickAuthBtn(ActionEvent actionEvent) {
        sendRequest();
    }

    public void intentRegistration(ActionEvent actionEvent) {
        Platform.runLater(this::openRegistrationWindow);
        Platform.runLater(() -> {
            registration.getScene().getWindow().hide();
        });
    }
}


