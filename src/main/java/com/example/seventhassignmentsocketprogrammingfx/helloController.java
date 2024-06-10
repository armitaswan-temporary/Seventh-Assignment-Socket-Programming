package com.example.seventhassignmentsocketprogrammingfx;

import com.example.seventhassignmentsocketprogrammingfx.Client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class helloController {

    // hello-view
    @FXML
    TextField usernameBTN;
    @FXML
    public String enterUsername() {
        return usernameBTN.getText();
    }

    // Client-Menu
    @FXML
    Button next1BTN;
    @FXML
    public void switchToClientMenu(ActionEvent event) throws IOException {
        if (Client.ClientSaver.getClient() == null) {
            Client client = new Client(enterUsername(), this);
            Client.ClientSaver.setClient(client);
            client.choice = 1; // Default to chat
            client.start();
        }
        loadScene("Client-Menu.fxml", event);
    }

    @FXML
    Button joinChatBTN;
    @FXML
    public void switchtoChat(ActionEvent event) throws IOException {
        Client client = Client.ClientSaver.getClient();
        if (client != null && client.isConnected()) {
            client.choice = 1;  // Set the choice for chat
            loadScene("Chat.fxml", event);
        } else {
            System.out.println("Client not connected");
        }
    }

    @FXML
    Button goDownloadBTN;
    @FXML
    public void switchtoDownload(ActionEvent event) throws IOException {
        Client client = Client.ClientSaver.getClient();
        if (client != null && client.isConnected()) {
            client.choice = 2;  // Set the choice for download
            loadScene("Download.fxml", event);
            addList();
        } else {
            System.out.println("Client not connected");
        }
    }

    @FXML
    Button exit;
    @FXML
    public void exitProgram() {
        Client client = Client.ClientSaver.getClient();
        if (client != null) {
            client.close();
        }
        Platform.exit(); // Closes JavaFX application
        System.exit(0);  // Terminates the JVM
    }

    // Chat
    @FXML
    Button back1BTN;
    @FXML
    VBox chatBox;
    @FXML
    TextField newMSGBTN;
    @FXML
    Button sendBTN;
    @FXML
    public void sendMSG() {
        Client client = Client.ClientSaver.getClient();
        String message = newMSGBTN.getText();
        if (!message.trim().isEmpty() && client != null) {
            client.sendMessage(message);
            addMSGToChatBox("You: " + message, true);
            newMSGBTN.clear();
        }
    }

    @FXML
    public void addMSGToChatBox(String message, boolean isUser) {
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setStyle(isUser ? "-fx-background-color: lightblue; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;" :
                "-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        chatBox.getChildren().add(msgLabel);
    }


    // Download
    @FXML
    Button back2BTN;
    @FXML
    VBox downloadList;
    @FXML
    TextField fileIndexBTN;
    @FXML
    Button next2BTN;
    @FXML
    public void switchToFileDownload(ActionEvent event) throws IOException {
        Client client = Client.ClientSaver.getClient();
        if (client != null && client.isConnected()) {
            client.startFileDownload(Integer.parseInt(fileIndexBTN.getText()));
            loadScene("Download.fxml", event);
        } else {
            System.out.println("Client not connected");
        }
    }
    public void addList() throws IOException {
        Client client = Client.ClientSaver.getClient();
        client.requestFileList();
        if (client != null && client.fileList != null) {
            Label msgLabel = new Label(client.fileList);
            msgLabel.setWrapText(true);
            msgLabel.setStyle("-fx-background-color: Green; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            downloadList.getChildren().add(msgLabel);
        }
    }

    // File-Download
    @FXML
    Label MSG1;
    @FXML
    public void showMSG1() {
        Client client = Client.ClientSaver.getClient();
        String message = client.downloadinStatus;
        if (message != null) {
            MSG1.setText(message);
        }
    }

    @FXML
    Label MSG2;
    @FXML
    public void showMSG2() {
        Client client = Client.ClientSaver.getClient();
        String message = client.fileName;
        if (client != null && message != null) {
            MSG1.setText(message);
        }
    }

    @FXML
    Button back3BTN;

    @FXML
    private void loadScene(String fxmlFile, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
