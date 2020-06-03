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

public class SignUpController implements Initializable {


    public Button signUpBtn;
    public TextField userSignUpLogin;
    public TextField userSignUpPass;
    public TextField userSignUpNick;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private static Network net = Network.getINSTANCE();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signUpBtn.setOnAction(actionEvent -> {
            try{
                net.connect("localhost", 8189);
                in = net.getInputStream();
                out = net.getOutputStream();
                String login = userSignUpLogin.getText();
                String pass = userSignUpPass.getText();
                String msg = "/regUser," + login + "," + pass;
                out.writeUTF(msg);
                out.flush();
                if(in.readUTF().startsWith("/regOk")) {
                    Platform.runLater(() -> {
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
                    });

                } else {
                    System.out.println("Something wrong =(");
//                    Stage stage = new Stage();
//                    Parent root = FXMLLoader.load(
//                            YourClassController.class.getResource("YourClass.fxml"));
//                    stage.setScene(new Scene(root));
//                    stage.setTitle("My modal window");
//                    stage.initModality(Modality.WINDOW_MODAL);
//                    stage.initOwner(
//                            ((Node)event.getSource()).getScene().getWindow() );
//                    stage.show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
