package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    public Button signUpBtn;
    public TextField userSignUpLogin;
    public TextField userSignUpPass;
    private DataOutputStream out;
    private DataInputStream in;
    private static final Network net = Network.getInstance();
    private double xOffset = 0;
    private double yOffset = 0;

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
        System.out.println(net.toString());
        Thread tr = new Thread(() -> {
            String msg;
            while (true) {
                try {
                    msg = in.readUTF();
                    System.out.println(msg);
                    if (msg.startsWith("/regOk")) {
                        Platform.runLater(this::openChatWindow);
                        Platform.runLater(() -> {
                            signUpBtn.getScene().getWindow().hide();
                        });
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        tr.setDaemon(true);
        tr.start();
    }

    public void clickSignUpBtn(ActionEvent actionEvent) {
        sendRequest();
    }

    public void closeWindow(MouseEvent keyEvent) throws IOException {
        in.close();
        out.close();
        Platform.exit();
        System.exit(0);
    }

    public void enterChat(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            sendRequest();
        }
    }
}
