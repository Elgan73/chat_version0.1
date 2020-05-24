import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickName;
    private boolean running;

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
        for (ClientHandler cl : Server.getClients()) {
            cl.sendMessage("/newClient " + this.nickName);
            this.sendMessage("/newClient " + cl.getNickName());
        }
        out.writeUTF("Hello " + nickName + "\n");
        out.flush();
    }

    public synchronized void deleteClient() throws Exception {
        Server.getClients().remove(this);
        for (ClientHandler cl : Server.getClients()) {
            cl.sendMessage("/deleteClient " + this.nickName);
        }
    }

    public synchronized void broadCastMessage(String message) throws IOException {
        for (ClientHandler client : Server.getClients()) {
                client.sendMessage(message);
        }
    }

    public synchronized void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    public void sendPrivateMsg(String name, String msg) throws IOException {
        for (ClientHandler cl : Server.getClients()) {
            if (name.equals(cl.getNickName())) {
                cl.sendMessage(this.nickName + " -> " + cl.getNickName() + ": " + msg);
                this.sendMessage(this.nickName + " -> " + cl.getNickName() + ": " + msg);
            }
        }
    }

    public synchronized void changeNickName(String msg) throws IOException {
        String nick = getNickName();
        setNickName(msg.substring(3));
        broadCastMessage(nick + " has changed nickname to " + nickName + "\n");
    }

    public synchronized void exitChat() throws Exception {
        Server.getClients().remove(this);
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
                    if(!clientMessage.isEmpty()) {
                        if (clientMessage.equals("/exit")) {
                            System.out.println(getNickName() + ": exit from chat");
                            exitChat();
                            break;
                        } else if (clientMessage.startsWith("/n")) {
                            changeNickName(clientMessage);
                        } else if (clientMessage.startsWith("@")) {
                            String msg = clientMessage.substring(1);
                            String[] user = msg.split(" ", 2);
                            sendPrivateMsg(user[0], user[1]);
                        } else {
                            broadCastMessage(getNickName() + ": " + clientMessage);
                        }
                    }

                    // authorization
//                        if (clientMessage.startsWith("/?")) {
//                            String loginMsg = clientMessage.substring(2);
//                            String[] logInPass = loginMsg.split(":", 2);
//                            getNickByLogin(logInPass[0], logInPass[1]);
//                            broadCastMessage("/authOk");
//                            System.out.println(logInPass[0] + " " + logInPass[1]);
//                        }
                    // registration
//                        if (clientMessage.startsWith("/!")) {
//                            String authMsg = clientMessage.substring(2);
//                            String[] authUser = authMsg.split(":", 3);
//                            setNickName(authUser[0]);
//
//                            System.out.println(authUser[0] + " " + authUser[1] + " " + authUser[2]);
//                        }


                    System.out.println(getNickName() + ": " + clientMessage);
                }
            } catch (EOFException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
                break;

            }
        }
    }
}


