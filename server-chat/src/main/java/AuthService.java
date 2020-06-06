import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AuthService implements Runnable {
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    static String urlConnectToDB = "jdbc:mysql://localhost:3306/chat";
    static String loginDB = "root";
    static String passDb = "12345678";

    private static ConcurrentLinkedDeque<ClientHandler> clients;

    public static ConcurrentLinkedDeque<ClientHandler> getClients() {
        return clients;
    }

    public AuthService(Socket socket, DataOutputStream out, DataInputStream in) {
        this.socket = socket;
        this.out = out;
        this.in = in;
    }

    public static boolean isUserDataConfirmed(String login, String pass) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String passFromDb = null;
        try (Connection connection = DriverManager.getConnection(urlConnectToDB, loginDB, passDb)) {
            PreparedStatement passwordRequest = connection.prepareStatement("select pass_usr from users where login_usr=?;");
            passwordRequest.setString(1, login);
            ResultSet resultSet = passwordRequest.executeQuery();

            if(resultSet.next()) {
                passFromDb = resultSet.getString("pass_usr");
            }

            if(passFromDb != null) {
                System.out.println("!!!!AAAAAAAAAAA");
            } else System.out.println("PUUUUUUSTOOOOOOOO");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("User was not found");
        }

        if (passFromDb != null) {
            return passFromDb.equals(pass);
        } else {
            return false;
        }
    }

    public static void createUser(String login, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String sql = "insert into users (login_usr, pass_usr) values ('" + login + "','" + pass + "');";
        try (Connection connection = DriverManager.getConnection(urlConnectToDB, loginDB, passDb)) {
            Statement statement = connection.createStatement();
            if (userAlreadyExist(login)) {
                System.out.println("Client exist!");
            } else {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean userAlreadyExist(String login) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection con = DriverManager.getConnection(urlConnectToDB, loginDB, passDb)) {
            PreparedStatement loginRequest = con.prepareStatement("select login_usr from users where login_usr = ?;");
            loginRequest.setString(1, login);
            ResultSet resultSet = loginRequest.executeQuery();
            if (resultSet != null) {
                System.out.println("user exist!");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public void run() {
        while (true) {
            try {
                if (socket.isConnected()) {
                    String clientMessage = in.readUTF();

                    System.out.println(clientMessage);

                    if (clientMessage.startsWith("/lp")) {
                        String[] splitMsg = clientMessage.split(",", 3);
                        if (isUserDataConfirmed(splitMsg[1], splitMsg[2])) {
                            System.out.println("Успешная авторизация");
                            out.writeUTF("/authOk");
                            out.flush();

                            clients = new ConcurrentLinkedDeque<>();
                            ClientHandler client = new ClientHandler(socket, splitMsg[1]);
                            clients.add(client);
                            System.out.println(client.getNickName() + " join us!");
                            new Thread(client).start();
                        } else System.out.println("Failed authorization =(");
                    } else  if (clientMessage.startsWith("/regUser")) {
                        String[] splitClientMessage = clientMessage.split(",", 3);
                        createUser(splitClientMessage[1], splitClientMessage[2]);
                        System.out.println("Успешная регистрация");
                        out.writeUTF("/regOk");
                        out.flush();

                        clients = new ConcurrentLinkedDeque<>();
                        ClientHandler client = new ClientHandler(socket, splitClientMessage[1]);
                        clients.add(client);
                        System.out.println(client.getNickName() + " join us!");
                        new Thread(client).start();
                    } else System.out.println("Failed registration =(!");
                }
            } catch (IOException e) {
                System.out.println("Неудачная попытка авторизации");
                try {
                    socket.close();
                    in.close();
                    out.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    break;
                }

                break;
            }
        }
    }
}
