package tcp;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Worker extends Thread {
    private Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


            System.out.println("SERVER: Processing client...");
            writer.write("Processing client...\n");
            writer.flush();

            String message = reader.readLine();

            while (true) {
                Server.increaseCounter();
                System.out.println("SERVER received: " + message);
                if (message.equals("log in")) {
                    writer.write("logged in\n");
                    writer.flush();

                    while (true) {
                        message = reader.readLine();
                        Server.increaseCounter();
                        System.out.println("SERVER received: " + message);
                        if (message.equals("log out")) {
                            writer.write("Logging out\n");
                            Thread.sleep(1000);
                            writer.flush();
                            return;
                        }
                        writer.write(message + "\n");
                        writer.flush();
                    }
                } else {
                    writer.write("You're not logged in! Closing connection!\n");
                    writer.flush();
                    Thread.sleep(1000);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                writer.flush();
                writer.close();
                reader.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class WebRequest {
        private String message;

        public WebRequest(String message) {
            this.message = message;
        }

        public static WebRequest of(BufferedReader reader) throws IOException {
            StringBuilder str = new StringBuilder();
            String line = "";
            while (!(line = reader.readLine()).equals("")) {
                str.append(line).append("\n");
            }
            String message = str.toString();
            return new WebRequest(message);
        }
    }
}
