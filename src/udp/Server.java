package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {
    private DatagramSocket socket;
    private byte[] buffer;

    public Server(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.buffer = new byte[256];
    }

    @Override
    public void run() {
        System.out.println("SERVER: starting ...");
        while (true) {
            try {
                buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("SERVER received: " + message);

                if (message.equals("login")) {
                    sendResponse("logged-in", packet);
                    handleClientSession(packet);
                } else {
                    sendResponse("not-logged-in-closing-connection", packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientSession(DatagramPacket packet) throws IOException {
        while (true) {
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("SERVER received: " + message);
            if (message.equals("logout")) {
                sendResponse("logged-out", packet);
                break;
            }

            sendResponse(message, packet);
        }
    }

    private void sendResponse(String response, DatagramPacket packet) throws IOException {
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, packet.getAddress(), packet.getPort());
        socket.send(responsePacket);
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        server.start();
    }
}
