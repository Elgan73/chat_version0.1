import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SrvApp {

    private final static int PORT = 8189;
    private String lastConnectedNickName;

    private static ConcurrentLinkedDeque<ClientHandler> clients;

    public static ConcurrentLinkedDeque<ClientHandler> getClients() {
        return clients;
    }

    public SrvApp(int port) {
        clients = new ConcurrentLinkedDeque<>();
        try (ServerSocket srv = new ServerSocket(port)) {
            System.out.println("Server started!");
            while (true) {
                Socket socket = srv.accept();

                if (auth(socket)) {
                    ClientHandler client = new ClientHandler(socket, lastConnectedNickName);
                    clients.add(client);
                    System.out.println(client.getNickName() + " accepted!");
                    new Thread(client).start();
                }
            }
        } catch (Exception e) {
            System.out.println("Неудачная попытка авторизации.");
        }
    }

    private boolean auth(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        while (true) {
            if (socket.isConnected()) {
                String clientMessage = in.readUTF();
                System.out.println(clientMessage);

                if (clientMessage.startsWith("/lp")) { //приватное сообщение
                    String[] splitClientMessage = clientMessage.split(",", 3);

                    if (isUserDataConfirmed(splitClientMessage[1], splitClientMessage[2])) {
                        System.out.println("Успешная авторизация");
                        out.writeUTF("/authOk");
                        out.flush();
                        lastConnectedNickName = splitClientMessage[1];
                        break;
                    } else {
                        out.writeUTF("/authorizationError");
                        out.flush();
                        System.out.println("Авторизация отклонена");
                    }
                } else if (clientMessage.startsWith("/regUser")) {
                    String[] splitMsg = clientMessage.split(",", 3);
                    if (createUser(splitMsg[1], splitMsg[2])) {
                        System.out.println("Регистрация прошла успешна");
                        out.writeUTF("/regOk");
                        out.flush();
                        lastConnectedNickName = splitMsg[1];
                        break;
                    } else {
                        System.out.println("Такой юзверь уже есть");
                    }
                }
            }
        }
        return true;
    }

    public boolean isUserDataConfirmed(String login, String password) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String passwordFromDB = null;

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/devapps4selling/IdeaProjects/chat/server-chat/starkchat.db")) {

            PreparedStatement passwordRequest = connection.prepareStatement("select pass_usr from users where login_usr = ?;");
            passwordRequest.setString(1, login);
            ResultSet resultSet = passwordRequest.executeQuery();
            passwordFromDB = resultSet.getString("pass_usr");


        } catch (SQLException throwable) {
            System.out.println("А юзверя-то и нет =(");
        }

        if (passwordFromDB != null) {
            return passwordFromDB.equals(password);
        } else {
            return false;
        }
    }

    public boolean createUser(String login, String pass) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String user = null;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/devapps4selling/IdeaProjects/chat/server-chat/starkchat.db")) {
            PreparedStatement createUserRequest = connection.prepareStatement("insert into users (login_usr, pass_usr) values (?, ?);");
            createUserRequest.setString(1, login);
            createUserRequest.setString(2, pass);
            createUserRequest.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(isUserDataConfirmed(login, pass)) {
            return true;
        } else {
            System.out.println("Проблема при записи в базу");
            return false;
        }

    }

    public boolean clientAlreadyExist(String login) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/devapps4selling/IdeaProjects/chat/server-chat/starkchat.db")) {
            String query = "select login_usr from users where login_usr = '" + login + "'";
            Statement st = connection.createStatement();
            ResultSet resultSet = st.executeQuery(query);
            while (resultSet.next()) {
                if (resultSet.getString("login_usr").equals(login)) {
                    System.out.println("isClientInDbName" + true);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changeNickName(String name, String newName) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/devapps4selling/IdeaProjects/chat/server-chat/starkchat.db")) {
            String changeNickName = "UPDATE users SET login_usr = '" + newName + "' WHERE login_usr = '" + name + "'";
            Statement st = connection.createStatement();
            if (!clientAlreadyExist(newName)) {
                st.execute(changeNickName);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        new SrvApp(PORT);
    }
}
