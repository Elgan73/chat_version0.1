import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

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
        out.writeUTF("Hello " + nickName + "\n");
        out.flush();
    }

    public void broadCastMessage(String message) throws IOException {
        for (ClientHandler client : Server.getClients()) {
//            if (!client.equals(this)) {
                client.sendMessage(message);
//            }
        }
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    public String name(String msg) {
        int b = msg.lastIndexOf("/");
        return msg.substring(3, b);
    }

    public void sendPrivateMsg(String name, String msg) throws IOException {
        for (ClientHandler cl : Server.getClients()) {
            if (cl.getNickName().equals(name)) {
                cl.broadCastMessage(msg);
            }
        }
    }

    public void sendSrvMessage(String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    @Override
    public void run() {

//        new Thread(() -> {
//            Scanner sc = new Scanner(System.in);
//            String msgSrv = sc.nextLine();
//            while (running) {
//                try {
//                    sendSrvMessage("Sauron: " + msgSrv + "\n");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        while (running) {
            try {

                if (socket.isConnected()) {
                    String clientMessage = in.readUTF();

                    if (clientMessage.equals("_exit_")) {
                        Server.getClients().remove(this);
                        sendMessage(clientMessage);
                        break;
                    }

                    if (clientMessage.contains("/n ")) {
                        setNickName(clientMessage.substring(3));

                        System.out.println("\n" + "Nickname has changed to " + nickName);
                        broadCastMessage("Nickname has changed to " + nickName);
                    }

                    if (clientMessage.contains("/w ")) {
                        String nickName = name(clientMessage);
                        sendPrivateMsg(nickName, clientMessage);
                    }
                    System.out.println(getNickName() + ": " + clientMessage);
                    broadCastMessage(getNickName() + ": " + clientMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
