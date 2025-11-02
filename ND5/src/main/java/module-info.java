module org.example.nd5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.crypto;


    opens org.example.nd5 to javafx.fxml;
    exports org.example.nd5;
}