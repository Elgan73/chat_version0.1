package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import javafx.stage.WindowEvent;
import net.Network;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public ListView<String> listView;
    public TextField inputText;
    public TextField nickName;
    public Button exitChat;
    public ListView<String> chatMsg;
    private DataInputStream in;
    private DataOutputStream out;
    private static final Network net = Network.getInstance();
    private File history;
    private String myNickName;

    public void send(MouseEvent actionEvent) {
        sendMessage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputText.requestFocus();
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
                if(message.startsWith("Hello")) {
                    String[] msg = message.split(" ", 2);
                    myNickName = msg[1];
                }
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
                    String msgFromSrv = message;
//                    listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//                        @Override
//                        public ListCell<String> call(ListView<String> stringListView) {
//                            return new ListCell<>() {
//                                @Override
//                                protected void updateItem(String item, boolean empty) {
//                                    super.updateItem(item, empty);
//                                    if(empty || item == null) {
//                                        return;
//                                    }
//                                    Date date = new Date();
//                                    SimpleDateFormat formatOfDate = new SimpleDateFormat("HH:mm");
//                                    String time = formatOfDate.format(date);
//                                    Label nick = new Label(myNickName);
//                                    Label tm = new Label(time);
//                                    Label clientMsg = new Label(msgFromSrv);
//                                    HBox hBox = new HBox(tm, clientMsg);
//                                    VBox vBox = new VBox(nick, hBox);
//                                    setGraphic(vBox);
//                                }
//                            };
//                        }
//                    });
                    Platform.runLater(() -> chatMsg.getItems().addAll(msgFromSrv));
                    writeMessageToFile(history, msgFromSrv);
                }

            } catch (IOException e) {
                chatMsg.getItems().addAll("Потеря связи с сервером");
                break;
            }
        }
    }

    private void sendMessage() {
        String msg = inputText.getText();
        if (!inputText.getText().isEmpty() && nickName.getText().isEmpty()) {
            try {
                out.writeUTF(msg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Date date = new Date();
            SimpleDateFormat formatOfDate = new SimpleDateFormat("HH:mm");
            String finalMessage = formatOfDate.format(date) + " " +  myNickName + " : " + inputText.getText();
//            listView.setCellFactory(new Callback<>() {
//                @Override
//                public ListCell<String> call(ListView<String> stringListView) {
//                    return new ListCell<>() {
//                        @Override
//                        protected void updateItem(String item, boolean empty) {
//                            super.updateItem(item, empty);
//                            if (item != null && !empty) {
//                                Date date = new Date();
//                                SimpleDateFormat formatOfDate = new SimpleDateFormat("HH:mm");
//                                String time = formatOfDate.format(date);
//                                Label nick = new Label(myNickName);
//                                Label tm = new Label(time);
//                                Label clientMsg = new Label(inputText.getText());
//                                HBox hBox = new HBox(tm, clientMsg);
//                                VBox vBox = new VBox(nick, hBox);
//                                setGraphic(vBox);
//                            } else {
//                                setGraphic(null);
//                            }
//
//                        }
//                    };
//                }
//            });
            chatMsg.getItems().addAll(finalMessage);
            writeMessageToFile(history, finalMessage);
        }

        if (inputText.getText().equals("") || inputText.getText().equals(" ")) {
            chatMsg.getItems().add("Вы не ввели сообщение");
        }

        if (!nickName.getText().isEmpty()) {
            String a = "@ " + nickName.getText() + " " + msg;
            Date date = new Date();
            SimpleDateFormat formatOfDate = new SimpleDateFormat("HH:mm");
            String privateMsg = formatOfDate.format(date) + " " + myNickName + " -> " + nickName.getText() + ": " + msg;
            try{
                out.writeUTF(a);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            chatMsg.getItems().add(privateMsg);
            writeMessageToFile(history, privateMsg);
        }
        inputText.clear();
        inputText.requestFocus();

    }

    private void readHistoryFromFile() {
        history = new File("/Users/devapps4selling/IdeaProjects/chat/client-chat/src/main/java/history/history.txt");
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

    public void sendEnter(KeyEvent keyEvent) {
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


    public void clearField(MouseEvent mouseEvent) {
        if(nickName.getText() != null) {
            nickName.clear();
        }
    }
}


