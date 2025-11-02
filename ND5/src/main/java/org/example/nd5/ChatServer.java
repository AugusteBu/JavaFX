package org.example.nd5;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatServer extends Application {

    private byte[] incoming = new byte[256];
    private final int PORT = 8886;

    private DatagramSocket socket;
    private ArrayList<Integer> users = new ArrayList<>();
    private InetAddress address;
    private TextArea messageArea;
    private TextField inputBox;

    private HashMap<String, List<Integer>> rooms = new HashMap<>();
    private HashMap<Integer, String> userNames = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        messageArea = new TextArea();
        inputBox = new TextField();
        messageArea.setMaxWidth(500);
        inputBox.setMaxWidth(500);
        messageArea.setEditable(false);

        Button dataButton = new Button("Data");
        dataButton.setOnAction(e -> saveChatData());

        VBox root = new VBox(messageArea, inputBox, dataButton);
        Scene scene = new Scene(root, 550, 300);

        primaryStage.setTitle("SERVER");
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            socket = new DatagramSocket(PORT);
            address = InetAddress.getByName("localhost");
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

        new Thread(this::runServer).start();

        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessageFromServer();
                inputBox.clear();
            }
        });
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

                String[] chatMessages = messageArea.getText().split("\n");

                for (String message : chatMessages) {
                    writer.println(message);
                }

                System.out.println("Chat data saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error saving chat data: " + e.getMessage());
            }
        }
    }
    private void runServer() {
        System.out.println("Server started on port " + PORT);

        while (true) {
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Server received: " + message);

            if (message.startsWith("init:")) {
                String[] parts = message.split(":");
                String identifier = parts[1];
                userNames.put(packet.getPort(), identifier);
                int port = packet.getPort();
                users.add(port);


                addUserToRoom("All", port);

                sendMessageToRoom("All", "server", identifier + " joined the room.", port);
            } else if (message.startsWith("room:")) {
                String[] parts = message.split(":");
                String roomName = parts[1].trim();
                int port = packet.getPort();
                addUserToRoom(roomName, port);

                sendMessageToRoom(roomName, "server", "User has joined the room.", port);
            } else if (message.startsWith("leave:")) {
                String[] parts = message.split(":");
                String roomName = parts[1].trim();
                int port = packet.getPort();
                removeUserFromRoom(roomName, port);
            } else {
                byte[] byteMessage = message.getBytes();

                String msg = new String(byteMessage);
                messageArea.appendText(msg + "\n");

                String[] parts = msg.split(" ", 2);
                String roomName = parts[0].substring(1, parts[0].length() - 1);
                String userMessage = parts[1];


                if (userMessage.contains("@")) {

                    String[] mentionedUsers = userMessage.split("@");

                    for (int i = 1; i < mentionedUsers.length; i++) {

                        String recipient = mentionedUsers[i].split("\\s", 2)[0];

                        for (Map.Entry<Integer, String> entry : userNames.entrySet()) {
                            if (entry.getValue().equals(recipient)) {
                                int recipientPort = entry.getKey();

                                DatagramPacket privateMessage = new DatagramPacket(byteMessage, byteMessage.length, address, recipientPort);
                                try {
                                    socket.send(privateMessage);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                            }
                        }
                    }


                } else {

                    if (rooms.containsKey(roomName)) {
                        for (int forwardPort : rooms.get(roomName)) {
                            if (forwardPort != packet.getPort()) {
                                DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forwardPort);
                                try {
                                    socket.send(forward);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    } else {
                        System.out.println("Room '" + roomName + "' does not exist.");
                    }
                }
            }
        }
    }

    private void removeUserFromRoom(String roomName, int port) {
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).remove(Integer.valueOf(port));
            if (rooms.get(roomName).isEmpty()) {
                rooms.remove(roomName);
            }
        } else {
            System.out.println("Room '" + roomName + "' does not exist.");
        }
    }

    private void sendMessageFromServer() {
        String message = inputBox.getText();
        inputBox.clear();
        if (!message.isEmpty()) {
            String formattedMessage = "server: " + message;
            byte[] byteMessage = formattedMessage.getBytes();
            for (int forwardPort : users) {
                DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forwardPort);
                try {
                    socket.send(forward);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            messageArea.appendText(formattedMessage + "\n");
            messageArea.positionCaret(messageArea.getText().length());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void addUserToRoom(String roomName, int port) {
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).add(port);
        } else {
            List<Integer> userList = new ArrayList<>();
            userList.add(port);
            rooms.put(roomName, userList);
        }
    }

    private void sendMessageToRoom(String roomName, String sender, String message, int senderPort) {
        String formattedMessage = "{" + roomName + "} " + sender + ": " + message;
        byte[] byteMessage = formattedMessage.getBytes();
        for (int forwardPort : rooms.get(roomName)) {
            if (forwardPort != senderPort) {
                DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forwardPort);
                try {
                    socket.send(forward);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        messageArea.appendText(formattedMessage + "\n");
        messageArea.positionCaret(messageArea.getText().length());
    }
}
