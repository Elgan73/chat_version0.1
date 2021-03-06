package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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

public class AuthController implements Initializable {
    public Button enterChat;
    public TextField userLogin;
    public TextField userPassword;
    private static final Network net = Network.getInstance();
    public ImageView closeWindow;
    private DataInputStream in;
    private DataOutputStream out;
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
            out.writeUTF("/lp," + userLogin.getText() + "," + userPassword.getText());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterChat(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            sendRequest();
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
                        Platform.runLater(() -> enterChat.getScene().getWindow().hide());
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Lost server connection");
                    break;
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void clickAuthBtn(ActionEvent actionEvent) {
        sendRequest();
    }

    public void closeWindow(MouseEvent keyEvent) throws IOException {
        in.close();
        out.close();
        Platform.exit();
        System.exit(0);
    }
}


