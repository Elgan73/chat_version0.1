import java.sql.*;

public class Database {
    Connection connection;

    public static void initDbDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public boolean addClient(String name, String password) {
        System.out.println("Adding client " + name + " / " + password);
        connection = openConnection();
        String createUser =
                "INSERT INTO USERS (login_usr, pass_usr) VALUES ('" + name + "','" + password + "');";
        try {
            try (Statement statement = connection.createStatement()) {
                System.out.println(createUser);
                if (isClientInDbByName(name)) {
                    System.out.println("Client exist!");
                    closeConnection(connection);
                    return false;
                } else {
                    statement.execute(createUser);
                    closeConnection(connection);
                    return true;
                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean changeNickName(String name, String newName) {
        connection = openConnection();
        String changeNickName = "UPDATE users SET login_usr = '" + newName +"' WHERE login_usr = '" + name +"'";
        try {
            try (Statement statement = connection.createStatement()) {
                if(isClientInDbByName(newName)) {
                    System.out.println("Client already exist!");
                    closeConnection(connection);
                    return false;
                } else {
                    statement.execute(changeNickName);
                    closeConnection(connection);
                    return true;
                }
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
        return false;
    }


    public boolean isClientInDbByName(String name) {

        connection = openConnection(); //2 connection
        String query = "SELECT name from USERS where name = '" + name + "'";
        try {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query);) {
                while (resultSet.next()) {
                    if (resultSet.getString("name").toLowerCase().equalsIgnoreCase(name)) {
                        System.out.println("isClientInDbByName " + true);
                        closeConnection(connection);//close 2nd
                        return true;
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        closeConnection(connection);
        return false;

    }

    public String[] getClientCredentialByName(String name) {
        String query = "SELECT login_usr, pass_usr from USERS where name = '" + name + "'";
        connection = openConnection();
        try {
            try (
                    Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery(query);) {
                while (result.next()) {
                    if (result.getString("name").toLowerCase().equalsIgnoreCase(name)) {
                        String username = result.getString("name");
                        String password = result.getString("password");
                        closeConnection(connection);
                        return new String[]{username, password};
                    }
                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("User does not exist.");
        closeConnection(connection);
        return new String[]{"", ""};
    }

    public Connection openConnection() {
        System.out.println("\t\tOpenning connection to DB...");

        try {
            connection = DriverManager.getConnection(
                    "com.mysql.cj.jdbc.Driver");
            if (connection.isClosed()) {
                connection = DriverManager.getConnection(
                        "com.mysql.cj.jdbc.Driver");
            } else {
                System.out.println("\t\tConnection open!");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public boolean closeConnection(Connection connection) {
        System.out.println("\t\tClosing Connection ...");
        try {
            if (!connection.isClosed()) {
                connection.close();
                System.out.println("\t\tConnection is closed - " + connection.isClosed());
                return connection.isClosed();
            }
            System.out.println("\t\tWAS already Closed by try()");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }
}
