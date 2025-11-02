package org.example.lab4;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class Data {
    private static ObservableList<Information> informationList = FXCollections.observableArrayList();

    public static ObservableList<Information> getInformationList() {
        return informationList;
    }


}