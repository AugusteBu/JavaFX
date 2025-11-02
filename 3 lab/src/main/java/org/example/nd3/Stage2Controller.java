package org.example.nd3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Stage2Controller {
    @FXML
    private Button btn1;

    @FXML
    private TextField txt1;

    @FXML
    private TextField txt2;

    @FXML
    private TextField txt3;
    @FXML
    private TextField txt4;

User users;
    User users2;
    public User getUsers2() {
        return users2;
    }

    public void setUsers2(User users2) {
        this.users2 = users2;
    }


    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }

    @FXML
    void btn1pressed(ActionEvent event) {
        String name = null,surname = null, color = null;
        User users2 = new User("", "", "");
        //singleton
       SingletonUser singletonUser = SingletonUser.getInstance();

        //template -> facade
        Template.PuttingInformation inf = new Template.PuttingInformation();
        inf.Information(users,users2);

        //factory
        Factory.Likes chosenColor = new Factory.Likes(users.getColor());


        txt1.setText(users2.getName());
        txt2.setText(users2.getSurname());
        txt3.setText(users2.getColor());
        txt4.setText(chosenColor.getColors().kindOfColor());
    }


}
