package com.example.seventhassignmentsocketprogrammingfx.Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;

    private PrintWriter output;
    private BufferedReader input;
    private String username;

    private static final String FILE_DIRECTORY = "D:\\AP\\Seventh-Assignment-Socket-Programming-fx\\src\\main\\java\\com\\example\\seventhassignmentsocketprogrammingfx\\Server\\data";

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            username = input.readLine();

            List<String> chatHistory = server.getChatHistory();
            for (String message : chatHistory) {
                output.println(message);
            }

            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                if (clientMessage.equals("REQUEST_FILE_LIST")) {
                    sendFileList();
                } else if (clientMessage.startsWith("DOWNLOAD_FILE:")) {
                    int fileIndex = Integer.parseInt(clientMessage.split(":")[1].trim()) - 1;
                    sendFile(fileIndex);
                } else {
                    String message = username + ": " + clientMessage;
                    System.out.println(message);
                    server.broadcastMessage(message, this);
                }
            }
        } catch (SocketException e) {
            if ("Socket closed".equals(e.getMessage())) {
                System.out.println("Client disconnected: " + username);
            } else {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void sendFileList() {
        File directory = new File(FILE_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                output.println((i + 1) + ": " + files[i].getName());
            }
            output.println("END_OF_LIST");
        } else {
            output.println("ERROR: No files available");
        }
    }

    private void sendFile(int fileIndex) {
        File directory = new File(FILE_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null && fileIndex >= 0 && fileIndex < files.length) {
            File file = files[fileIndex];
            try (FileInputStream fileInput = new FileInputStream(file);
                 BufferedOutputStream socketOutput = new BufferedOutputStream(socket.getOutputStream())) {
                output.println("START_FILE_TRANSFER:" + file.getName());
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    socketOutput.write(buffer, 0, bytesRead);
                }
                socketOutput.flush();
                output.println("File download complete");
            } catch (IOException e) {
                e.printStackTrace();
                output.println("ERROR: Failed to send file");
            }
        } else {
            output.println("ERROR: File not found");
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    private void close() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
            server.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
