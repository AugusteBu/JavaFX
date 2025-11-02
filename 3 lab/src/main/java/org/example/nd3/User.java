package org.example.nd3;

public class User {
    String Name, Surname, Color;

    public void setName(String name) {
        Name = name;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public void setColor(String color) {
        Color = color;
    }

    public User(String name, String surname, String color) {
        this.Name = name;
        this.Surname = surname;
        this.Color = color;
    }


    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getColor() {
        return Color;
    }
}
