import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends BaseAuthService implements Runnable{

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
        out.writeUTF("Hello " + nickName + "\n");
        out.flush();
    }

    public void broadCastMessage(String message) throws IOException {
        for (ClientHandler client : Server.getClients()) {
            client.sendMessage(message);
        }
    }

    public synchronized void broadCastClientList() throws IOException {
        StringBuilder sb = new StringBuilder("/clients ");
        for(ClientHandler client : Server.getClients()) {
            sb.append(client.getNickName() + " ");
        }
        broadCastMessage(sb.toString());

    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    public void sendPrivateMsg(String name, String msg) {
        for (ClientHandler cl : Server.getClients()) {
            if (name.equals(cl.getNickName())) {
                String mess = cl.getNickName() + " -> " + msg;
                try {
                    cl.sendMessage(mess + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void changeNickName(String msg) throws IOException {
        String nick = getNickName();
        setNickName(msg.substring(3));
        broadCastMessage("\n" + nick + " has changed nickname to " + nickName + "\n");
    }

    public void exitChat() {
        Server.getClients().remove(this);
        this.downService();
    }

    public void downService() {
        try{
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored){}
    }

    @Override
    public void run() {
        while (running) {
            try {
                String clientMessage = in.readUTF();
                if (socket.isConnected()) {
                    broadCastClientList();

                    if (clientMessage.startsWith("/n")) {
                        changeNickName(clientMessage);
                    }

                    if(clientMessage.startsWith("/?")) {
                        String loginMsg = clientMessage.substring(2);
                        String[] logInPass = loginMsg.split(":", 2);
                        getNickByLogin(logInPass[0], logInPass[1]);
                        broadCastMessage("/authOk");
                        System.out.println(logInPass[0] + " " + logInPass[1]);
                    }

                    if(clientMessage.startsWith("/!")) {
                        String authMsg = clientMessage.substring(2);
                        String[] authUser = authMsg.split(":", 3);
                        setNickName(authUser[0]);

                        System.out.println(authUser[0] + " " + authUser[1] + " " + authUser[2]);
                    }

                    if (clientMessage.equals("/exit")) {
                        System.out.println(getNickName() + ": exit from chat");
                        exitChat();
                        break;
                    }

                    if (clientMessage.startsWith("@")) {
                        String msg = clientMessage.substring(1);
                        String[] user = msg.split(" ", 2);
                        sendPrivateMsg(user[0], user[1]);
                    } else {
                        broadCastMessage(getNickName() + ": " + clientMessage);
                    }

//                    System.out.println(getNickName() + ": " + clientMessage);
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;

            }
        }
    }
}
