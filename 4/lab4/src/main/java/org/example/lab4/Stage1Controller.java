package org.example.lab4;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Stage1Controller {

    @FXML
    private Button btn1;
    @FXML
    private Button btn2;
    @FXML
    private Button btn3;
    @FXML
    private Button btn4;
    @FXML
    private Button btn5;
    @FXML
    private MenuButton choice2;
    @FXML
    private TableView<Information> table;
    @FXML
    private TextField txt1;
    @FXML
    private TextField txt2;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtUntil;
    @FXML
    private TextField txt3;
    @FXML
    private TextField txt4;
    @FXML
    private TextField txt5;
    @FXML
    private TextField txt6;
    @FXML
    private Button btn6;
    @FXML
    private TextField txt7;
    @FXML
    private TextField txt8;
    @FXML
    private TableColumn<Information, String> Grupe;
    @FXML
    private TableColumn<Information, Integer> Nr;
    @FXML
    private TableColumn<Information, String> Pavarde;
    @FXML
    private TableColumn<Information, String> Vardas;
    private ObservableList<Information> list = FXCollections.observableArrayList();
    private FilteredList<Information> filteredList;
    private ObservableList<Information> informationList = Data.getInformationList();
    private int nextNumber = list.size() + 1;

    @FXML
    public void initialize() {
        Nr.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNr()).asObject());
        Pavarde.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSurname()));
        Vardas.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        Grupe.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGroup()));

        Nr.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        Pavarde.setCellFactory(TextFieldTableCell.forTableColumn());
        Vardas.setCellFactory(TextFieldTableCell.forTableColumn());
        Grupe.setCellFactory(TextFieldTableCell.forTableColumn());

        Nr.setOnEditCommit(event -> {
            Information information = event.getRowValue();
            information.setNr(event.getNewValue());
            table.refresh();
        });

        Pavarde.setOnEditCommit(event -> {
            Information information = event.getRowValue();
            information.setSurname(event.getNewValue());
            table.refresh();
        });

        Vardas.setOnEditCommit(event -> {
            Information information = event.getRowValue();
            information.setName(event.getNewValue());
            table.refresh();
        });

        Grupe.setOnEditCommit(event -> {
            Information information = event.getRowValue();
            information.setGroup(event.getNewValue());
            table.refresh();
        });

        table.setEditable(true);
        table.setItems(informationList);
    }

    @FXML
    void btn1pressed(ActionEvent event) {   // PRIDETI
        int lastStudentNumber = list.isEmpty() ? 0 : list.get(list.size() - 1).getNr();

        Information information = new Information(lastStudentNumber + 1, txt2.getText(), txt3.getText(), txt4.getText(), attendanceDays);
        list.add(information);
        informationList.add(information);
        Collections.sort(list, Comparator.comparingInt(Information::getNr));

        if (filteredList == null) {
            table.getItems().setAll(list);
        } else {
            filteredList = new FilteredList<>(list, filteredList.getPredicate());
            table.setItems(filteredList);
        }

        txt2.clear();
        txt3.clear();
        txt4.clear();
    }


    private Stage primaryStage;

    int attendanceDays = 0;

    @FXML
    void btn2pressed(ActionEvent event) {     //IKELTI EXCEL

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                boolean headerSkipped = false;

                while ((line = reader.readLine()) != null) {
                    if (!headerSkipped) {
                        headerSkipped = true;
                        String[] headers = line.split(",");         //istrinti header ir sukurti nauja
                        line = reader.readLine();
                        headers = line.split(",");
                        attendanceDays = headers.length - 4;

                        boolean columnsExist = checkAttendanceColumnsExist();

                        if (!columnsExist) {
                            for (int i = 0; i < attendanceDays; i++) {
                                String columnName = String.valueOf(i + 1);
                                TableColumn<Information, Boolean> attendanceColumn = createAttendanceColumn("Day " + columnName);
                                table.getColumns().add(attendanceColumn);
                            }
                        }
                    }
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        try {
                            int nr = Integer.parseInt(parts[0].trim());
                            String pavarde = parts[1].trim();
                            String vardas = parts[2].trim();
                            String grupe = parts[3].trim();

                            int[] attendanceData = new int[parts.length - 4];
                            for (int i = 0; i < attendanceData.length; i++) {
                                attendanceData[i] = Integer.parseInt(parts[i + 4].trim());
                            }
                            Information information = new Information(nr, pavarde, vardas, grupe, attendanceDays);
                            information.setAttendanceData(attendanceData);

                            list.add(information);

                        } catch (NumberFormatException e) {
                            System.err.println("Invalid data format: " + line);
                        }
                    }
                }

                table.getItems().setAll(list);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean checkAttendanceColumnsExist() {
        for (TableColumn<Information, ?> column : table.getColumns()) {
            if (column.getText().startsWith("Day")) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void btn3pressed(ActionEvent event) {       //ISTIRNTI
        int nrToDelete = Integer.parseInt(txt7.getText());
        Iterator<Information> iterator = list.iterator();
        while (iterator.hasNext()) {
            Information information = iterator.next();
            if (information.getNr() == nrToDelete) {
                iterator.remove();
                for (Information info : list) {
                    if (info.getNr() > nrToDelete) {
                        info.setNr(info.getNr() - 1);
                    }
                }
                nextNumber--;
                break;
            }
        }

        if (filteredList != null) {
            filteredList.setPredicate(null);
            filteredList.removeIf(info -> info.getNr() == nrToDelete);
        }

        table.getItems().clear();
        if (filteredList != null) {
            table.getItems().setAll(filteredList);
        } else {
            table.getItems().setAll(list);
        }

        txt7.clear();
    }
    int AttendanceNumber = 1;

    @FXML
    public void btn4pressed(ActionEvent event) { // add day
        attendanceDays++;

        TableColumn<Information, Boolean> attendanceColumn = createAttendanceColumn("Day " + attendanceDays);

        table.getColumns().add(attendanceColumn);
        for (Information info : informationList) {
            BooleanProperty attendanceProperty = new SimpleBooleanProperty(false);
            info.getAttendance().add(attendanceProperty);
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional(attendanceProperty);
        }
        AttendanceNumber++;
    }
    private TableColumn<Information, Boolean> createAttendanceColumn(String title) {
        TableColumn<Information, Boolean> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> {
            Information info = data.getValue();
            int index = Integer.parseInt(title.substring(title.lastIndexOf(" ") + 1)) - 1;
            return info.getAttendance().get(index);
        });

        column.setCellFactory(CheckBoxTableCell.forTableColumn(column));
        return column;
    }
    @FXML
    void btn5pressed(ActionEvent event) {
        String surnameFilter = txt5.getText();
        String groupFilter = txt6.getText();

        if (surnameFilter.isEmpty() && groupFilter.isEmpty()) {
            if (filteredList != null) {
                filteredList = null;

                table.setItems(list);
            }
        } else {

            if (filteredList == null) {
                filteredList = new FilteredList<>(list);
                table.setItems(filteredList);
            }

            filteredList.setPredicate(info -> {
                boolean surnameMatch = surnameFilter.isEmpty() || info.getSurname().contains(surnameFilter);
                boolean groupMatch = groupFilter.isEmpty() || info.getGroup().contains(groupFilter);
                return surnameMatch && groupMatch;
            });
        }

        txt5.clear();
        txt6.clear();
    }

    @FXML
    void btn6pressed(ActionEvent event) {
        for (int i = 1; i <= attendanceDays; i++) {
            boolean studentsPresent = false;
            for (Information info : informationList) {
                if (info.getAttendance().size() >= i && info.getAttendance().get(i - 1).get()) {
                    studentsPresent = true;
                    break;
                }
            }
            TableColumn<Information, ?> dayColumn = getColumnByTitle("Day " + i);
            if (dayColumn != null) {
                dayColumn.setVisible(studentsPresent);
            }
        }
    }

    private TableColumn<Information, ?> getColumnByTitle(String title) {
        for (TableColumn<Information, ?> column : table.getColumns()) {
            if (column.getText().equals(title)) {
                return column;
            }
        }
        return null;
    }

    @FXML
    void btn7pressed(ActionEvent event) {

        for (TableColumn<Information, ?> column : table.getColumns()) {
            column.setVisible(true);
        }
    }

    @FXML
    void btn8pressed(ActionEvent event) {
        int from = Integer.parseInt(txtFrom.getText());
        int until = Integer.parseInt(txtUntil.getText());

        String filename = "Student_Attendance.pdf";
        try (OutputStream outputStream = new FileOutputStream(filename)) {
            PrintWriter writer = new PrintWriter(outputStream);
            writer.println("%PDF-1.5");
            writer.println("1 0 obj");
            writer.println("<< /Type /Catalog /Pages 2 0 R >>");
            writer.println("endobj");
            writer.println("2 0 obj");
            writer.println("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
            writer.println("endobj");
            writer.println("3 0 obj");
            writer.println("<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 4 0 R >> /ProcSet [/PDF /Text] >> /MediaBox [0 0 612 792] /Contents 5 0 R >>");
            writer.println("endobj");
            writer.println("4 0 obj");
            writer.println("<< /Type /Font /Subtype /Type1 /Name /F1 /BaseFont /Helvetica >>");
            writer.println("endobj");
            writer.println("5 0 obj");
            writer.println("<< /Length 76 >>");
            writer.println("stream");
            writer.println("BT");
            writer.println("/F1 12 Tf");
            System.out.println("from: " + from + ", until: " + until);

            int yOffset = 720;
            for (Information info : table.getItems()) {
                StringBuilder infoString = new StringBuilder();
                infoString.append(info.getNr()).append(" ").append(info.getName()).append(" ").append(info.getSurname()).append(" ").append(info.getGroup()).append(" ");

                for (int i = from; i <= until; i++) {
                    if (i <= info.getAttendance().size()) {
                        infoString.append(info.getAttendance().get(i - 1).get() ? "1 " : "0 ").append(", ");
                    }
                }

                if (infoString.length() > 2) {
                    infoString.setLength(infoString.length() - 2);
                }

                writer.println("BT");
                writer.println("/F1 12 Tf");
                writer.println("72 " + yOffset + " Td");
                writer.println("(" + infoString.toString() + ") Tj");
                writer.println("ET");

                yOffset -= 20;
            }

            writer.println("ET");
            writer.println("endstream");
            writer.println("endobj");
            writer.println("xref");
            writer.println("0 6");
            writer.println("0000000000 65535 f ");
            writer.println("0000000009 00000 n ");
            writer.println("0000000074 00000 n ");
            writer.println("0000000125 00000 n ");
            writer.println("0000000172 00000 n ");
            writer.println("0000000455 00000 n ");
            writer.println("trailer");
            writer.println("<< /Size 6 /Root 1 0 R >>");
            writer.println("startxref");
            writer.println("570");
            writer.println("%%EOF");

            writer.flush();
            writer.close();

            System.out.println("PDF file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void btn9pressed(ActionEvent event) {

        String filename = "Student_Attendance.csv";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Number,Name,Surname,Group,Attendance\n");
            for (Information info : list) {
                int nr = info.getNr();


                    writer.write(nr + "," + info.getName() + "," + info.getSurname() + "," + info.getGroup() + "," + info.getAttendanceString() + "\n");
            }
            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}