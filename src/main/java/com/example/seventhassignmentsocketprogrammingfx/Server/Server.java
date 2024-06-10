package com.example.seventhassignmentsocketprogrammingfx.Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private final int port;
    private final Set<ClientHandler> clientHandlers;
    private final List<String> chatHistory;

    private static final int MAX_CHAT_HISTORY = 50;

    public Server(int port) {
        this.port = port;
        this.clientHandlers = Collections.synchronizedSet(new HashSet<>());
        this.chatHistory = Collections.synchronizedList(new ArrayList<>());
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) {
        chatHistory.add(message);
        if (chatHistory.size() > MAX_CHAT_HISTORY) {
            chatHistory.remove(0);
        }

        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler != sender) {
                    clientHandler.sendMessage(message);
                }
            }
        }
    }

    public synchronized List<String> getChatHistory() {
        return new ArrayList<>(chatHistory);
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public static void main(String[] args) {
        int port = 333;
        Server server = new Server(port);
        server.start();
    }
}
