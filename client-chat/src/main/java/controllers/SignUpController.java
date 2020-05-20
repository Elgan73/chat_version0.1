package controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {


    public Button signUpBtn;
    public TextField userSignUpLogin;
    public TextField userSignUpPass;
    public TextField userSignUpNick;
    private DataOutputStream out;
    private Socket socket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signUpBtn.setOnAction(actionEvent -> {
            try{
                socket = new Socket("localhost", 8189);
                out = new DataOutputStream(socket.getOutputStream());
                String login = userSignUpLogin.getText();
                String pass = userSignUpPass.getText();
                String nickName = userSignUpNick.getText();
                String msg = "/!" + login + ":" + pass + ":" + nickName;
                out.writeUTF(msg);
                out.flush();
                signUpBtn.getScene().getWindow().hide();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
