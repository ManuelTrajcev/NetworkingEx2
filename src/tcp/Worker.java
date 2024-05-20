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

            String message = reader.readLine();

            System.out.println("Processing client...");
            if (message.equals("log in")) {
                writer.write("logged in\n");
                writer.flush();
                while (true) {
                    Server.increaseCounter();
                    message = reader.readLine();
                    if (message.equals("log out")) {
                        writer.write("Logging out\n");
                        writer.flush();
                        break;
                    }
                    writer.write(message + "\n");
                    writer.flush();
                    System.out.println("SERVER received: " + message);
                }
            } else {
                writer.write("You're not logged in!\n");
                writer.write("Closing connection!!\n");
                writer.flush();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
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
