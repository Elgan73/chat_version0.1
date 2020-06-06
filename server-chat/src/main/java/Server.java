
import model.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class Server {

    static Map<ClientHandle, String> clientMap = new ConcurrentHashMap<>();
    private final static int PORT = 8189;

    private static ConcurrentLinkedDeque<Client> allClients = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<ClientHandle> clientsList;

//    public static ConcurrentLinkedDeque<ClientHandler> getClients() {
//        return clients;
//    }

    protected void printOnlineClientsList() {
        System.out.println(clientsList);
    }

    protected void printAllClients() {
        System.out.println(allClients);
    }

    public Server(int port) throws ClassNotFoundException {

        System.out.println("Server is started...");
        dbInit();
        serverInit();

//        try {
//            ServerSocket srv = new ServerSocket(PORT);
//            System.out.println("Server started!");
//            while (true) {
//                Socket socket = srv.accept();
//
//                DataInputStream in = new DataInputStream(socket.getInputStream());
//                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//
////                AuthService authService = new AuthService(socket, out, in);
////                new Thread(authService).start();
////                if(socket.isConnected()) {
////                    ClientHandler clientHandler = new ClientHandler(socket, nickName);
////                    clients.add(clientHandler);
////                    new Thread(clientHandler).start();
////                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }


    }

    private void dbInit() {
        try {
            Database.initDbDriver();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        Database database = new Database();
    }

    private void serverInit() {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            clientsList = new ConcurrentLinkedDeque<>();

            while (true) {
                System.out.println("Ждем новые подключения...");
                Socket socket = serverSocket.accept();
                System.out.println("Новый клиент =)");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Сервер остановлен");
    }

    public String clientAsString() {
        StringBuilder strBld = new StringBuilder();
        for (ClientHandle cl : clientsList) {
            strBld.append(cl.getClientName()).append("|");
        }
        return strBld.toString();
    }

    public void subscribe(ClientHandle clientHandle) {
        clientsList.add(clientHandle);
        clientMap.put(clientHandle, "");
        broadCastMsg(clientHandle.getClientName() + " присоединился к чату");

    }

    public void unSubscribe(ClientHandle clientHandle) {
        clientsList.remove(clientHandle);
        clientMap.remove(clientHandle);
        broadCastClientList();
    }

    public void broadCastClientList() {
        StringBuilder sb = new StringBuilder("/newClient ");
        for(ClientHandle cl : clientsList) {
            sb.append(cl.getClientName()).append(" ");
        }
        broadCastMsg(sb.toString());

    }

    public void broadCastMsg(String msg) {
        for(ClientHandle cl : clientsList) {
            cl.sendMsg(cl.getClientName() + " " + msg);
        }
    }

    public void sendPrivateMessage(String msg, ClientHandle clientHandle) {
        String tmpMsg = msg.substring(1);
        String[] clientMsg = tmpMsg.split(" ", 3);
        for(ClientHandle cl : clientsList) {
            if(cl.getClientName().equals(clientMsg[1])) {
                cl.sendMsg("Private from " + clientHandle.getClientName() + " " + msg);
                return;
            }
        }
    }






    public static void main(String[] args) throws ClassNotFoundException {
        new Server(PORT);
    }
}
