package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class JoinController {
    public TextField loginJoinField;
    public Button joinBtn;
    private DataOutputStream out;

    public void joinChat(ActionEvent actionEvent) throws IOException {
        if (!loginJoinField.getText().isEmpty()) {
            Socket socket = new Socket("localhost", 8189);
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("/n " + loginJoinField.getText());
            out.flush();
            joinBtn.getScene().getWindow().hide();
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
    }
}
