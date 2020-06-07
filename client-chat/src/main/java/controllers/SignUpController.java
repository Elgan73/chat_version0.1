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

public class SignUpController implements Initializable {


    public Button signUpBtn;
    public TextField userSignUpLogin;
    public TextField userSignUpPass;
    private DataOutputStream out;
    private DataInputStream in;
    private static Network net = Network.getINSTANCE();

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
            out.writeUTF("/regUser," + userSignUpLogin.getText() + "," + userSignUpPass.getText());
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

        Thread tr = new Thread(() -> {
            String msg;
            while (true) {

                try {
                    msg = in.readUTF();

                    if (msg.startsWith("/regOk")) {
                        Platform.runLater(this::openChatWindow);
                        Platform.runLater(() -> {
                            signUpBtn.getScene().getWindow().hide();
                        });
                        break;
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
            }

        });
        tr.setDaemon(true);
        tr.start();


    }

    public void clickSignUpBtn(ActionEvent actionEvent) {
        sendRequest();
    }
}
