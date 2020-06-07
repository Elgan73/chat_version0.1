package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import net.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    ObservableList<String> clients = FXCollections.observableArrayList();
    @FXML
    public ListView<String> listView = new ListView<>(clients);
    public TextField inputText;
    public TextField nickName;
    public Button exitChat;
    public ListView<String> chatMsg;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static Network net = Network.getINSTANCE();


    public void send(ActionEvent actionEvent) {
        sendMessage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        net.connect("localhost", 8189);
        in = net.getInputStream();
        out = net.getOutputStream();

        Thread t1 = new Thread(() -> {
            while (true) {
                try {
                    listView.setItems(clients);
                    listView.refresh();
                        inputText.requestFocus();
                    String message = in.readUTF();

                    if (message.startsWith("/newClient") || message.startsWith("/u")) {
                        String[] s = message.split(" ", 2);
                        Platform.runLater(() -> listView.getItems().addAll(s[1]));
                    }

                    doubleClickLVItems();

                    if (message.startsWith("/deleteClient")) {
                        Platform.runLater(() -> listView.getItems().remove(message.substring(14)));
                    }

                    if (message.equals("/exit")) {
                        quitChat();
                        break;
                    }
                    if (!message.isEmpty() && !message.contains("/n") && !message.startsWith("@") && !message.contains("/delete")) {
                        Platform.runLater(() -> chatMsg.getItems().add(message));
                    }
                    System.out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.setDaemon(true);
        t1.start();
    }

    public void sendMessage() {
        String msg = inputText.getText();
        try {

            if (!inputText.getText().isEmpty() && nickName.getText().isEmpty()) {
                out.writeUTF(msg);
                out.flush();
            }
            if (inputText.getText().equals("") || inputText.getText().equals(" ")) {
                Platform.runLater(() -> chatMsg.getItems().add("Вы не ввели сообщение"));
            }

            if (inputText.getText().equals("/exit")) {
                socket.close();
                out.close();
                in.close();
            }

            if (!nickName.getText().isEmpty()) {
                String a = "@" + nickName.getText() + " " + msg;
                out.writeUTF(a + "\n");
                out.flush();
            }
            inputText.clear();
            inputText.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doubleClickLVItems() {
        listView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                String cl = listView.getSelectionModel().getSelectedItems().get(0);
                nickName.setText(cl);
            }
        });
    }

    public void sendEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            sendMessage();
        }
    }

    public void quitChat() {
        Platform.exit();
    }

    public void exitChat(ActionEvent actionEvent) throws IOException {
        out.writeUTF("/exit");
        out.flush();
        Platform.exit();
        System.exit(0);
    }

    public static EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }

    private static EventHandler<WindowEvent> closeEventHandler =
            event -> {
                System.out.println("DO SMTH ON EXIT");
                System.exit(1);
            };
}
