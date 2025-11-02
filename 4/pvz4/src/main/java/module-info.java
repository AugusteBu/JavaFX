module org.example.pvz4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.pvz4 to javafx.fxml;
    exports org.example.pvz4;
}