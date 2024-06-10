package com.example.seventhassignmentsocketprogrammingfx.Client;

import com.example.seventhassignmentsocketprogrammingfx.helloController;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private Socket socket;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;

    private final String serverAddress = "localhost";
    private final int serverPort = 333;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private helloController controller;

    private String username;
    private boolean connected = false;

    public int choice;
    public String fileList = null;
    public String downloadinStatus;
    public String fileName;
    private static final String DOWNLOAD_DIRECTORY = "D:\\AP\\Seventh-Assignment-Socket-Programming-fx\\src\\main\\java\\com\\example\\seventhassignmentsocketprogrammingfx\\Client";

    public Client(String username, helloController controller) {
        this.username = username;
        this.connected = true;
        this.controller = controller;
        this.fileList = null;
    }

    public void start() {
        try {
            socket = new Socket(serverAddress, serverPort);
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the server.");

            serverOutput.println(username);

            if (choice == 1) {
                System.out.println("Entering Chat...");
                startGroupChat();
            } else if (choice == 2) {
                System.out.println("Requesting file list...");
                requestFileList();
            }

            // Keep listening to the server
            new Thread(new ServerListener()).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void requestFileList() throws IOException {
        serverOutput.println("REQUEST_FILE_LIST");
        StringBuilder fileListBuilder = new StringBuilder();
        String fileListLine;
        while ((fileListLine = serverInput.readLine()) != null) {
            if (fileListLine.equals("END_OF_LIST")) {
                break;
            }
            fileListBuilder.append(fileListLine).append("\n");
        }
        fileList = fileListBuilder.toString();
    }

    public void startGroupChat() {
        new Thread(() -> {
            try {
                String message;
                while ((message = messageQueue.take()) != null) {
                    if (message.equals("0")) {
                        start();
                        return;
                    }
                    System.out.println("You: " + message);
                    serverOutput.println(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void sendMessage(String message) {
        if (!message.trim().isEmpty()) {
            messageQueue.offer(message);
        }
    }

    public void startFileDownload(int fileIndex) {
        serverOutput.println("DOWNLOAD_FILE:" + fileIndex);
        new Thread(() -> {
            try {
                String serverResponse = serverInput.readLine();
                if (serverResponse.startsWith("START_FILE_TRANSFER:")) {
                    String fileName = serverResponse.split(":")[1];
                    receiveFile(fileName);
                    downloadinStatus = serverResponse;
                    this.fileName = fileName;
                } else if (serverResponse.equals("ERROR: File not found")) {
                    downloadinStatus = serverResponse;
                } else {
                    downloadinStatus = "Unexpected server response: " + serverResponse;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void receiveFile(String fileName) {
        try (FileOutputStream fileOutput = new FileOutputStream(DOWNLOAD_DIRECTORY + File.separator + fileName);
             InputStream socketInput = socket.getInputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = socketInput.read(buffer)) != -1) {
                fileOutput.write(buffer, 0, bytesRead);
            }
            System.out.println("File downloaded successfully.");
            downloadinStatus = "File downloaded successfully.";
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to download file.");
            downloadinStatus = "Failed to download file.";
        }
    }

    public void close() {
        try {
            connected = false;
            if (serverInput != null) serverInput.close();
            if (serverOutput != null) serverOutput.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    private class ServerListener implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = serverInput.readLine()) != null) {
                    String finalMessage = message;
                    Platform.runLater(() -> controller.addMSGToChatBox(finalMessage, false));
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static class ClientSaver {
        private static Client client;

        public static Client getClient() {
            return client;
        }

        public static void setClient(Client newClient) {
            client = newClient;
        }
    }
}
