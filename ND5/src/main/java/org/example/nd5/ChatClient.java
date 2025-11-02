package org.example.nd5;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ChatClient extends Application {

    private static final DatagramSocket socket;

    static {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static final InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static String identifier;

    private static final int SERVER_PORT = 8886;

    private static final TextArea messageArea = new TextArea();

    private static final TextField inputBox = new TextField();

    private static final ChoiceBox<String> roomChoiceBox = new ChoiceBox<>();

    private static String currentRoom = "All";


    private static final Map<String, Integer> userPorts = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        identifier = scanner.nextLine();

        ClientThread clientThread = new ClientThread(socket, messageArea);
        clientThread.start();

        byte[] uuid = ("init:" + identifier).getBytes();
        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
        socket.send(initialize);

        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        messageArea.setMaxWidth(500);
        messageArea.setEditable(false);

        inputBox.setMaxWidth(500);
        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String messageText = inputBox.getText().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                    inputBox.clear();
                }
            }
        });

        roomChoiceBox.setMaxWidth(150);
        roomChoiceBox.getItems().add("All");
        roomChoiceBox.setValue("All");
        roomChoiceBox.setOnAction(event -> {
            currentRoom = roomChoiceBox.getValue();
        });

        Button addRoomButton = new Button("Add room");
        addRoomButton.setOnAction(e -> addRoom());

        HBox inputControls = new HBox(inputBox, addRoomButton, roomChoiceBox);
        Button dataButton = new Button("Data");
        dataButton.setOnAction(e -> saveChatData());
        HBox buttonBox = new HBox(dataButton);

        Label connectedAsLabel = new Label("Connected as: " + identifier);
        VBox topBox = new VBox(5, connectedAsLabel, buttonBox);

        Scene scene = new Scene(new VBox(10, topBox, messageArea, inputControls), 550, 300);
        primaryStage.setTitle("ChatClient");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveChatData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setTitle("Save Chat Data");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("=== Chat Data ===");
                writer.println("Timestamp: " + LocalDateTime.now());
                writer.println("----------------------------");

                String chatData = messageArea.getText();

                writer.println(chatData);

                System.out.println("Chat data saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error saving chat data: " + e.getMessage());
            }
        }
    }
    private static void sendMessage(String messageText) {
        int colonIndex = messageText.indexOf(":");
        if (colonIndex != -1) {
            String recipient = messageText.substring(0, colonIndex).trim();
            String messageContent = messageText.substring(colonIndex + 1).trim();
            String temp = "{" + currentRoom + "} " + identifier + " to " + recipient + ": " + messageContent;


            messageArea.appendText(temp + "\n");


            byte[] msg = temp.getBytes();
            DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
            try {
                socket.send(send);
                messageArea.positionCaret(messageArea.getText().length());
            } catch (IOException e) {
                messageArea.appendText("Error sending message to server\n");
                e.printStackTrace();
            }
        } else {

            String temp = "{" + currentRoom + "} " + identifier + ": " + messageText;
            messageArea.appendText(temp + "\n");
            byte[] msg = temp.getBytes();
            DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
            try {
                socket.send(send);
                messageArea.positionCaret(messageArea.getText().length());
            } catch (IOException e) {
                messageArea.appendText("Error sending message to server\n");
                e.printStackTrace();
            }
        }
    }

    private static void addRoom() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Room");
        dialog.setHeaderText("Enter room name:");
        dialog.setContentText("Room name:");

        dialog.showAndWait().ifPresent(roomName -> {
            roomChoiceBox.getItems().add(roomName);

            String message = "room:" + roomName;
            byte[] byteMessage = message.getBytes();
            DatagramPacket sendRoom = new DatagramPacket(byteMessage, byteMessage.length, address, SERVER_PORT);
            try {
                socket.send(sendRoom);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void updateUserPort(String user, int port) {
        userPorts.put(user, port);
    }
}
