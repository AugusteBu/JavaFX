module org.example.nd3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.nd3 to javafx.fxml;
    exports org.example.nd3;
}