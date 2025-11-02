package org.example.nd3;

public class Facade{

    public static class UserFacade {
        private String name;
        private String surname;
        private String color;



        public UserFacade(String name, String surname, String color) {
            createUserInformation(name, surname, color);
        }

        private void createUserInformation(String name, String surname, String color) {
            this.name = name;
            this.surname = surname;
            this.color = color;
        }
        public String getName() {
            return name;
        }
        public String getSurname() {
            return surname;
        }
        public String getColor() {
            return color;
        }

    }



    public static class Name {
        private String name;

        public Name(String name) {
            this.name = name;
        }


    }

    public static class Surname {
        private String surname;

        public Surname(String surname) {
            this.surname = surname;
        }


    }

    public static class Color {
        private String color;

        public Color(String color) {
            this.color = color;
        }


    }



}
