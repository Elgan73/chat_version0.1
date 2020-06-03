
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    private final static int PORT = 8189;

    public Server(int port) {

        try {
            ServerSocket srv = new ServerSocket(PORT);
            System.out.println("Server started!");
            while (true) {
                Socket socket = srv.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                AuthService authService = new AuthService(socket, out, in);
                new Thread(authService).start();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }



    }

    public static void main(String[] args) {
        new Server(PORT);
    }
}
