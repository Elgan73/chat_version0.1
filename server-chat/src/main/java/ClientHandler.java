import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickName;
    private boolean running;
    private SrvApp srvApp;


    public ClientHandler(Socket socket, String nickName) throws IOException {
        this.socket = socket;
        this.nickName = nickName;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        running = true;

        welcome();

    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void welcome() throws IOException {
        for (ClientHandler cl : SrvApp.getClients()) {
            cl.sendMessage("/newClient " + this.nickName);
            this.sendMessage("/newClient " + cl.getNickName());
        }
        out.writeUTF("Hello " + nickName);
        out.flush();
    }

    public synchronized void deleteClient() throws Exception {
        SrvApp.getClients().remove(this);
        for (ClientHandler cl : SrvApp.getClients()) {
            cl.sendMessage("/deleteClient " + this.nickName);
        }
    }

    public synchronized void broadCastMessage(String message) throws IOException {
        for (ClientHandler client : SrvApp.getClients()) {
            if (!client.equals(this)) {
                client.sendMessage(message);
            }
        }
    }



    public void sendPrivateMsg(String name, String msg) throws IOException {
        for (ClientHandler cl : SrvApp.getClients()) {
            if (name.equals(cl.getNickName())) {
                cl.sendMessage(this.nickName + " -> " + cl.getNickName() + ": " + msg);
            }
        }
    }

    public synchronized void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    public synchronized void changeNickName(String msg) throws IOException {
        String oldNick = getNickName();
        String newNick = msg.substring(3);

        if (srvApp.changeNickName(oldNick, newNick)) {
            broadCastMessage(oldNick + " has changed nickname to " + nickName + "\n");
        } else {
            sendMessage("Вы не можете выбрать это Ник, он уже занят");
        }

    }

    public synchronized void exitChat() throws Exception {
        SrvApp.getClients().remove(this);
        deleteClient();
        this.downService();
    }


    public synchronized void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        while (running) {
            try {

                if (socket.isConnected()) {
                    String clientMessage = in.readUTF();
                    if (!clientMessage.isEmpty()) {
                        if (clientMessage.equals("/exit")) {
                            System.out.println(getNickName() + ": exit from chat");
                            sendMessage(clientMessage);
                            exitChat();
                            break;
                        } else if (clientMessage.startsWith("/n")) {
                            changeNickName(clientMessage);
                        } else if (clientMessage.startsWith("@")) {
                            String msg = clientMessage.substring(2);
                            System.out.println(msg);
                            String[] user = msg.split(" ", 2);
                            sendPrivateMsg(user[0], user[1]);
                        } else {
                            broadCastMessage(getNickName() + ": " + clientMessage);
                        }
                    }
                    System.out.println(getNickName() + ": " + clientMessage);
                }
            } catch (Exception ex) {
                System.out.println("Потеряна связь с клиентом: " + this.nickName);

                try {
                    socket.close();
                    in.close();
                    out.close();
                    deleteClient();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}


