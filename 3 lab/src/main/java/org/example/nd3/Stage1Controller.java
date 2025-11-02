package org.example.nd3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Stage1Controller {
    @FXML
    private Button btn1;

    @FXML
    private TextField txt1;

    @FXML
    private TextField txt2;

    @FXML
    private TextField txt3;

    @FXML
    void btn1pressed(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        User user = new User(txt1.getText(),txt2.getText(), txt3.getText());

        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Stage2Controller.class.getResource("Stage2.fxml"));
        Stage2Controller controller = new Stage2Controller();

        fxmlLoader.setController(controller);
        controller.setUsers(user);

        Parent root;            // pagrindinis langas
        root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
}