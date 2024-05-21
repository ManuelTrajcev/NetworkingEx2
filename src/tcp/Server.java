package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;
    private static int msgCounter;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("SERVER: starting...");
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("SERVER: started successfully!");
        System.out.println("SERVER: waiting for connections...");

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("SERVER: new client accepted!");
            new Worker(socket).start();
        }
    }

    public static synchronized void increaseCounter() {
        msgCounter++;
        System.out.println("SERVER: total messages received: " + msgCounter);
    }

    public static void main(String[] args) {
        int port = 7000;
        Server server = new Server(port);
        server.start();
    }
}
