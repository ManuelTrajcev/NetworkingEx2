package tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client extends Thread {
    private int serverPort;
    private List<String> messages;

    public Client(int serverPort) {
        this.serverPort = serverPort;
        messages = new ArrayList<>();
        messages.add("log in");
        messages.add("log out");
        messages.add("hello");
        messages.add("how are you");
        messages.add("good morning");
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        Random random = new Random();

        try {
            socket = new Socket(InetAddress.getLocalHost(), this.serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("CLIENT: starting...");
//            writer.write("log in\n");
//            writer.flush();

            String response = reader.readLine();
            System.out.println(response);

            while (true) {
                writer.write(messages.get(random.nextInt(5)));
                writer.write("\n");
                writer.flush();

                response = reader.readLine();
                System.out.println("Client received: " + response);
                if (response.equals("Logging out") || response.equals("You're not logged in! Closing connection!")){
                    return;
                }
                Thread.sleep(1000);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client(7000);
        client.start();
    }
}
