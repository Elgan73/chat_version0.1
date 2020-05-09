import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public ListView listView;
    public TextField inputText;
    public TextField nickName;
    public TextArea chatMsg;
    private Socket socket;
    private DataInputStream in;

    public void send(ActionEvent actionEvent) throws IOException {
        sendMessage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());

            new Thread(() -> {
                while (true) {
                    try {
                        chatMsg.appendText(in.readUTF());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() throws IOException {
        String msg = inputText.getText();
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                if (inputText != null) {
                    out.writeUTF(msg + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        inputText.clear();
        inputText.requestFocus();
    }

    public void sendPrivateMessage(String name, String message) {

    }

    public void sendEnter(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }
}
