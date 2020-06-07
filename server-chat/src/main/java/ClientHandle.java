import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class ClientHandle {

    Database database;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String clientName;
    private boolean isLoggedIn;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ClientHandle(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.clientName = "";
            this.isLoggedIn = false;
            this.database = new Database();
            new Thread(() -> {
                try {
                    authentication();
                    readMsg();
                } finally {
                    exitChat();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы с созданием обработчика");
        }
    }

    public void welcome() {

    }

    public void authentication() {
        while (!isLoggedIn) {
            String clientMessage = getMsgFromClient();
            if (!clientMessage.isEmpty()) {

                if(clientMessage.startsWith("/exit")) {
                    sendMsg("/exit");
                    exitChat();
                    return;
                }

                if(clientMessage.startsWith("/lp")) {
                    if(isLoggedIn) {
                        sendMsg("User already authorized");
                        continue;
                    }
                    authorization(clientMessage);
                }

                if (clientMessage.startsWith("/regUser")) {
                    registration(clientMessage);
                }

            }
        }
    }

    public boolean authorization(String msg) {
        String[] command = msg.split(",", 3);
        String login = command[1];
        String pass = command[2];

        if(!database.isClientInDbByName(login)) {
            sendMsg("User not exist. Please sign up!");
            return false;
        } else if(!database.getClientCredentialByName(login)[1].equals(pass)) {
            sendMsg(login + " password is wrong!");
            return false;
        } else {
            setClientName(login);
            isLoggedIn = true;
            sendMsg("/authOk " + login + " successfully authorized");

            server.subscribe(this);
//            sendMsg("/newClient " + this.clientName);
            return true;
        }
    }

    public void registration(String msg) {
        Connection connection = database.openConnection();
        try{
            System.out.println("Connection is close" + connection.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] command = msg.split(",", 3);
        String login = command[1];
        String pass = command[2];
        if(!isTaken(login)) {
            database.addClient(login, pass);
            sendMsg("/regOk " + login + " successfully registered");
            server.subscribe(this);
//            sendMsg("/newClient " + this.clientName);

        } else {
            sendMsg("NickName: " + login + " is busy");
        }

        System.out.println("Connect is close" + database.closeConnection(connection));
    }

    public void readMsg() {
        while (true) {
            String strFromClient = getMsgFromClient();

            if(strFromClient.startsWith("/exit")) {
                System.out.println(getClientName() + ": exit from chat");
                exitChat();
                break;
            } else if(strFromClient.startsWith("/n")) {
                changeNickName(strFromClient);
            } else if(strFromClient.startsWith("@")) {
                server.sendPrivateMessage(strFromClient, this);
            }
        }
    }

    private boolean isTaken(String clientName) {
        return database.isClientInDbByName(clientName);
    }


    private void changeNickName(String msg) {
        String tmp = msg.substring(2);
        String[] clMsg = tmp.split(" ", 2);
        database.changeNickName(clMsg[0], clMsg[1]);
    }

    private String getMsgFromClient() {
        String strFromClient ;
        try {
            strFromClient = in.readUTF();
            System.out.println(strFromClient);
        } catch (IOException e) {
            // если поток обрывается вместо клиента пишем /exit
            System.out.println("handle IOException in getMsgFromClient() ");
            strFromClient = "/exit";
        }
        return strFromClient;
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitChat() {
        server.unSubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseMsg(String msg) {
        if(msg.startsWith("/")) {
            parseCommand(msg);
        } else {
            server.broadCastMsgWithoutSender(msg, this);
        }
    }

    public void parseCommand(String msg) {
//        if(msg.startsWith("/"))
    }


}
