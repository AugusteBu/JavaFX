package org.example.nd3;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
public class Factory {

    @FXML
    private static TextField txt4;


    abstract static class Colors {
        public abstract String kindOfColor();
    }

    static class Blue extends Colors {
        @Override
        public String kindOfColor() {
            return "Danguje";
        }
    }

    static class Green extends Colors {
        @Override
        public String kindOfColor() {
            return "Žoleje";
        }
    }

    public static class Other extends Colors {
        @Override
        public String kindOfColor() {
            return "Paveiksle";
        }
    }

     static class Likes {
        private Colors colors;

        public Likes(String type) {
            if (type != null) {
                if (type.equals("melyna") || type.equals("mėlyna")||type.equals("violetinė")||type.equals("violetinė")) {
                    colors = new Blue();
                } else if (type.equals("zalia")||type.equals("lapai")||type.equals("žalia")) {
                    colors = new Green();
                } else {
                    colors = new Other();
                }
            }
            else{
                colors = new Other();
            }
        }

         public Colors getColors() {
             return colors;
         }


     }


}