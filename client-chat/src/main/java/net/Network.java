package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private static Network instance = new Network();
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String ipAddress;
    private Socket clientSocket;
    private int port;

    private Network() {
    }


    public boolean connect(String ipAddress, int port) {
        try {
            Socket clientSocket = new Socket(ipAddress, port);
            instance.setInputStream(new DataInputStream(clientSocket.getInputStream()));
            instance.setOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
            instance.setClientSocket(clientSocket);
            instance.setIpAddress(ipAddress);
            instance.setPort(port);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    private boolean isConnect;


    public static Network getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Network{" +
                "outputStream=" + outputStream +
                ", inputStream=" + inputStream +
                ", clientSocket=" + clientSocket +
                ", port=" + port +
                ", isConnect=" + isConnect +
                '}';
    }
}
