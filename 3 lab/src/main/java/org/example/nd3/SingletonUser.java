package org.example.nd3;

public class  SingletonUser {
    private static  SingletonUser instance;

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    private SingletonUser() {


    }


    public static SingletonUser getInstance() {
        if(instance == null)
        {
            instance = new SingletonUser();
        }
        return instance;
    }
    public User getUser() {
        return user;
    }

}
