module org.example.nd6 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.nd6 to javafx.fxml;
    exports org.example.nd6;
}