package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import net.Network;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    //    ObservableList<String> clients = FXCollections.observableArrayList();
    public ListView<String> listView;
    public TextField inputText;
    public TextField nickName;
    public Button exitChat;
    public ListView<String> chatMsg;
    private DataInputStream in;
    private DataOutputStream out;
    private static final Network net = Network.getInstance();
    private File history;


    public void send(ActionEvent actionEvent) throws IOException {
        sendMessage();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        inputText.requestFocus();

        net.connect("localhost", 8189);
        in = net.getInputStream();
        out = net.getOutputStream();

        doubleClickLVItems();
        readHistoryFromFile();

        Thread t = new Thread(this::readMsg);
        t.setDaemon(true);
        t.start();
    }

    public void readMsg() {
        String message;
        while (true) {
            try {
                message = in.readUTF();
                System.out.println(message);
                if (message.startsWith("/")) {
                    if (message.equals("/exit")) {
                        in.close();
                        out.close();
                        break;
                    }
                    if (message.startsWith("/newClient")) {
                        String[] s = message.split(" ", 2);
                        Platform.runLater(() -> listView.getItems().addAll(s[1]));
                    }
                    if (message.startsWith("/deleteClient")) {
                        String finalMessage1 = message;
                        Platform.runLater(() -> listView.getItems().remove(finalMessage1.substring(14)));
                    }
                } else {

                    String finalMessage = message;
                    Platform.runLater(() -> chatMsg.getItems().addAll("HAHAHHAHAH " + finalMessage));
                    writeMessageToFile(history, finalMessage);
                }

            } catch (IOException e) {
                chatMsg.getItems().addAll("Потеря связи с сервером");
                break;
            }
        }
    }

    public void sendMessage() throws IOException {
        String msg = inputText.getText();

        if (!inputText.getText().isEmpty() && nickName.getText().isEmpty()) {
            out.writeUTF(msg);
            out.flush();
            Platform.runLater(() -> chatMsg.getItems().addAll(msg));
        }

        if (inputText.getText().equals("") || inputText.getText().equals(" ")) {
            Platform.runLater(() -> chatMsg.getItems().add("Вы не ввели сообщение"));
        }

        if (!nickName.getText().isEmpty()) {
            String a = "@" + nickName.getText() + " " + msg;
            out.writeUTF(a + "\n");
            out.flush();
        }
        writeMessageToFile(history, msg);
        inputText.clear();
        inputText.requestFocus();

    }

    private void readHistoryFromFile() {
        history = new File("chat-client/history.txt");

        try {
            history.createNewFile();
            BufferedReader readHistoryFromFile = new BufferedReader(new FileReader(history));
            while(true) {
                String line = readHistoryFromFile.readLine();
                if(line == null){
                    break;
                }
                chatMsg.getItems().addAll(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения истории сообщений");
        }
    }

    private void writeMessageToFile(File history, String Message) {
        //Запись полученного сообщения в файл
        try (PrintWriter saveMessageToFile = new PrintWriter(new FileOutputStream(history,true))) {
            saveMessageToFile.println(Message);
            saveMessageToFile.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void doubleClickLVItems() {
        listView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                String cl = listView.getSelectionModel().getSelectedItems().get(0);
                nickName.setText(cl);
            }
        });
    }

    public void sendEnter(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            sendMessage();
        }
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
