package org.example.pvz4;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));


        VBox vBox = new VBox();
        Button btn = new Button("ok");
        btn.setOnAction(e->{System.out.println("df");});
        DatePicker tt = new DatePicker();

        TableView tableview = new TableView();
        TableColumn<String, String> tableColumn1 = new TableColumn<>("Studentas");
        TableColumn<String, String> tableColumn2 = new TableColumn<>("Lankomumas");

        tableview.getColumns().addAll(tableColumn1,tableColumn2);

        vBox.getChildren().addAll(btn, tt,tableview);

                                //<--------
        Scene scene = new Scene(vBox, 700, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


//kam naudojamas, reikalingas? interface ir abstraktas?
//abtrakti klase reikalnga - heritint negali?
//INTERAFCE- galima implementinti daug interface'u  /   susitarimas, kuris metodus privalodu turesime vykdyti

//pvz, nori pasidaryti komponenta (txt, buttons etc)
//su dizainu daryti
//lentele skirsis so columns priklausys nuo skaiciaus


// ABSTRACT CLASS -