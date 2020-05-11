import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public ListView<String> listView;
    public TextField inputText;
    public TextField nickName;
    public TextArea chatMsg;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;



    public void send(ActionEvent actionEvent) {
        sendMessage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t1 = new Thread(() -> {
                while (true) {
                    try {
                        chatMsg.appendText(in.readUTF() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            socket.close();
                            in.close();
                            out.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    }
                }
            });
            t1.setDaemon(true);
            t1.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        String msg = inputText.getText();

        try {
            if (inputText.getText().equals("") || inputText.getText().equals(" ")) {
                chatMsg.appendText("Вы не ввели сообщение" + "\n");
            }

            if (inputText.getText().equals("/exit")) {
                socket.close();
                out.close();
                in.close();
            }
            if (!nickName.getText().isEmpty()) {
                String a = "@" + nickName.getText() + " " + msg;
                String b = nickName.getText() + " -> " + msg + "\n";
                out.writeUTF(a + "\n");
                out.flush();
            } else {
                out.writeUTF(msg);
                out.flush();
            }
            inputText.clear();
            inputText.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                out.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void sendEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

}
