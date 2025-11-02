package org.example.nd6;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HelloController implements Initializable {
    @FXML
    private DatePicker Date1;
    @FXML
    private DatePicker Date2;
    @FXML
    private TableColumn<PersonInfo, String> country;
    @FXML
    private TableColumn<PersonInfo, LocalDate> birth_date;
    @FXML
    private TableColumn<PersonInfo, String> domain_name;

    @FXML
    private TableColumn<PersonInfo, String> email;

    @FXML
    private TableColumn<PersonInfo, String> first_name;

    @FXML
    private TableColumn<PersonInfo, String> gender;

    @FXML
    private TableColumn<PersonInfo, String> id;
    @FXML
    private TableColumn<PersonInfo, String> last_name;
    @FXML
    private Label Status;
    @FXML
    private TableView<PersonInfo> table;
    @FXML
    private Button btn1;
    private ObservableList<PersonInfo> data = FXCollections.observableArrayList();
    @FXML
    void Date1pressed(ActionEvent event) {
        filterData();
    }

    @FXML
    void Date2pressed(ActionEvent event) {
        filterData();
    }

    @FXML
    void filterData() {
        LocalDate startDate = Date1.getValue();
        LocalDate endDate = Date2.getValue();

        ObservableList<PersonInfo> filteredData = data.stream()
                .filter(person -> startDate == null || person.getBirth_date().compareTo(startDate) >= 0)
                .filter(person -> endDate == null || person.getBirth_date().compareTo(endDate) <= 0)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        table.setItems(filteredData);

/*
        List<String> name = data.stream()
                .filter(s->s.getId().startsWith("1"))
                .map(PersonInfo::getFirst_name)
                .toList();

        */
    }

    @FXML
    void bt1pressed() {
        Status.setText("Status: Uploading");
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 3; i++) {
            final int fileNumber = i;
            executor.execute(() -> {
                try {
                    loadData("MOCK_DATA" + fileNumber + ".csv");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        Thread completionThread = new Thread(() -> {
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                Platform.runLater(() -> {
                    Status.setText("Status: Completed");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        completionThread.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        first_name.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        last_name.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        country.setCellValueFactory(new PropertyValueFactory<>("country"));
        domain_name.setCellValueFactory(new PropertyValueFactory<>("domain_name"));
        birth_date.setCellValueFactory(new PropertyValueFactory<>("birth_date"));
        table.setItems(data);
    }
    private void loadData(String filename) throws IOException {
        ObservableList<PersonInfo> fileData = FXCollections.observableArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 8) {
                    PersonInfo personInfo = new PersonInfo(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], LocalDate.parse(parts[7], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    Platform.runLater(() -> {
                        data.addAll(personInfo);
                    });
                    Thread.sleep(10);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }



    @FXML
    void btn2pressed(ActionEvent event) {       //sorted A to Z
        ObservableList<PersonInfo> sortingData = data.stream()
                .sorted(Comparator.comparing(PersonInfo::getFirst_name))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        table.setItems(sortingData);
    }


    @FXML
    void btn3pressed(ActionEvent event) { //sorted Z to A
            ObservableList<PersonInfo> sortingData = data.stream()
                    .sorted(Comparator.comparing(PersonInfo::getFirst_name).reversed())
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            table.setItems(sortingData);
        }



    @FXML
    void btn4pressed(ActionEvent event) {///sort 1 to ..
            ObservableList<PersonInfo> sortingData = data.stream()
                    .sorted(Comparator.comparingInt(person -> Integer.parseInt(person.getId())))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            table.setItems(sortingData);
        }

    @FXML
    void btn5pressed(ActionEvent event) {///sort ... to 1
        ObservableList<PersonInfo> sortingData = data.stream()
                .sorted(Comparator.comparingInt(person -> Integer.parseInt(person.getId())))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        Collections.reverse(sortingData);
        table.setItems(sortingData);
    }


}
