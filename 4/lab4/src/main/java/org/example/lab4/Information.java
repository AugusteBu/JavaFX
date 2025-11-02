package org.example.lab4;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Information extends AbstractClass {
    int Number;
    private ObservableList<BooleanProperty> attendance = FXCollections.observableArrayList();
    String Name, Surname, Group;
    public ObservableList<BooleanProperty> getAttendance() {
        return attendance;
    }


    public String getAttendanceString() {
        StringBuilder attendanceString = new StringBuilder();
        for (BooleanProperty attendanceProperty : attendance) {
            attendanceString.append(attendanceProperty.get() ? "1 " : "0 ").append(", ");
        }

        if (attendanceString.length() > 2) {
            attendanceString.setLength(attendanceString.length() - 2);
        }
        return attendanceString.toString();
    }

    public void setAttendanceData(int[] attendanceData) {
        this.attendance.clear();
        for (int data : attendanceData) {
            this.attendance.add(new SimpleBooleanProperty(data != 0));
        }
    }


    public Information(int nr, String name, String surname, String group, int numDays) {
        this.Number = nr;
        this.Name = name;
        this.Surname = surname;
        this.Group = group;
        this.attendance = FXCollections.observableArrayList();
        for (int i = 0; i < numDays; i++) {
            this.attendance.add(new SimpleBooleanProperty(false));
        }
    }
    public int getNr() {
        return Number;
    }

    public void setNr(int nr) {
        Number = nr;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }


}
