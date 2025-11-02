package org.example.nd5;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChatRoom extends  Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private String identifier;
    private String roomName;
    public ChatRoom(DatagramSocket socket, InetAddress address, int port, String identifier, String roomName) {
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.identifier = identifier;
        this.roomName = roomName;
    }




    @Override
    public void run() {
        System.out.println("Joined room: " + roomName);
    }


    public class Room {
        private String roomName;
        private VBox roomUI;

        public Room(String roomName) {
            this.roomName = roomName;
            this.roomUI = createRoomUI();
        }

        private VBox createRoomUI() {
            VBox roomBox = new VBox();
            roomBox.setStyle("-fx-border-color: black; -fx-padding: 5px;");

            Label roomLabel = new Label(roomName);
            roomLabel.setStyle("-fx-font-weight: bold;");

            roomLabel.setOnMouseClicked(event -> {

                System.out.println("Editing room name: " + roomName);
            });

            roomBox.getChildren().add(roomLabel);

            return roomBox;
        }

        public VBox getRoomUI() {
            return roomUI;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
    }

}
