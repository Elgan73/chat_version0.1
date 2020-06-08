
import model.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class Server {

    private final static int PORT = 8189;

    private static ConcurrentLinkedDeque<ClientHandle> clientsList;

    public static ConcurrentLinkedDeque<ClientHandle> getClientsList() {
        return clientsList;
    }

    public Server(int port) {

        System.out.println("Server is started...");
        dbInit();
        serverInit();
    }

    private void dbInit() {
        try {
            Database.initDbDriver();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void serverInit() {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            clientsList = new ConcurrentLinkedDeque<>();

            while (true) {
                System.out.println("Ждем новые подключения...");
                Socket socket = serverSocket.accept();
                System.out.println("Новый клиент =)");
                new ClientHandle(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Сервер остановлен");
    }


//    public String clientAsString() {
//        StringBuilder strBld = new StringBuilder("/newClient ");
//        for (ClientHandle cl : getClientsList()) {
//            strBld.append(cl.getClientName()).append(",");
//        }
//        return strBld.toString();
//    }

    public void subscribe(ClientHandle clientHandle) {
        clientsList.add(clientHandle);
        broadCastMsg(clientHandle.getClientName() + " присоединился к чату");
        broadCastClientList();

    }

    public void unSubscribe(ClientHandle clientHandle) {
        clientsList.remove(clientHandle);
        broadCastClientList();
    }

    public void broadCastClientList() {
        StringBuilder sb = new StringBuilder("/newClient ");
        for(ClientHandle cl : getClientsList()) {
            sb.append(cl.getClientName()).append(" ");
        }
        broadCastMsg(sb.toString());

    }

    public void broadCastMsg(String msg) {
        for(ClientHandle cl : getClientsList()) {
            cl.sendMsg(cl.getClientName() + ": " + msg);
        }
    }

    public void broadCastMsgWithoutSender(String msg, ClientHandle client) {
        ConcurrentLinkedDeque<ClientHandle> tmpList = new ConcurrentLinkedDeque<>(clientsList);
        tmpList.remove(client);
        for(ClientHandle cl : tmpList) {
            cl.sendMsg(msg);
        }
    }

    public void sendPrivateMessage(String msg, ClientHandle clientHandle) {
        String tmpMsg = msg.substring(1);
        String[] clientMsg = tmpMsg.split(" ", 3);
        for(ClientHandle cl : getClientsList()) {
            if(cl.getClientName().equals(clientMsg[1])) {
                cl.sendMsg("Private from " + clientHandle.getClientName() + " " + msg);
                return;
            }
        }
    }


    public static void main(String[] args) {
        new Server(PORT);
    }
}
