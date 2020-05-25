import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Server {
    private final static int PORT = 8189;
    private static int cnt = 1;
    private BaseAuthService baseAuthService;

    private boolean isRunning = true;
    private static ConcurrentLinkedDeque<ClientHandler> clients;

    public static ConcurrentLinkedDeque<ClientHandler> getClients() {
        return clients;
    }

    public Server(int port) {

        clients = new ConcurrentLinkedDeque<>();
        try {
            ServerSocket srv = new ServerSocket(PORT);
            System.out.println("Server started!");
            while (isRunning) {
                Socket socket = srv.accept();
                ClientHandler client = new ClientHandler(socket, "client" + cnt);
                clients.add(client);
                System.out.println(client.getNickName() + " accepted!");
                new Thread(client).start();
                cnt++;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }



    }

    public static void main(String[] args) {
        new Server(PORT);
    }
}
