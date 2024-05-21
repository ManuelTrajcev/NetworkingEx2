package udp;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client extends Thread {
    private String serverName;
    private int serverPort;

    private DatagramSocket socket;
    private InetAddress address;
    private List<String> messages;
    private byte[] buffer;

    public Client(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;

        try {
            this.socket = new DatagramSocket();
            this.address = InetAddress.getByName(serverName);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        messages = new ArrayList<>();
        messages.add("login");
        messages.add("logout");
        messages.add("hello");
        messages.add("how-are-you");
        messages.add("good-morning");
    }

    @Override
    public void run() {
        Random random = new Random();
        DatagramPacket packet = null;

        while (true) {
            try {
                buffer = messages.get(random.nextInt(5)).getBytes();
                packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                socket.send(packet);
                buffer = new byte[256];
                packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("CLIENT received: " + new String(packet.getData(), 0, packet.getLength()));
                if (message.equals("logged-out") || message.equals("not-logged-in-closing-connection")) {
                    return;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 5000);
        client.start();
    }
}
