package model;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private static int count;
    String name;
    String pass;
    int id;
    static List<Client> clientList = new ArrayList<>();

    public Client(String name, String pass, int id) {
        count++;
        this.name = name;
        this.pass = pass;
        this.id = count;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }
}
