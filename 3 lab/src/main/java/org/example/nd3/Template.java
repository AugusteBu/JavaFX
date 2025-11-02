package org.example.nd3;

public class Template {


    static class PuttingInformation {

        public final void Information(User user, User user2) {
            Facade.UserFacade facade = new Facade.UserFacade(user.getName(),user.getSurname(),user.getColor());
            //facade.createUserInformation();
            PuttingName(user2,facade);
            PuttingSurname(user2, facade);
            PuttingColor(user2, facade);
        }

        public void PuttingName(User user2, Facade.UserFacade facade ) {
            user2.setName(facade.getName());
        }
        public void PuttingSurname(User user2,Facade.UserFacade facade ) {
            user2.setSurname(facade.getSurname());
        }
        public void PuttingColor(User user2,Facade.UserFacade facade ) {
           user2.setColor(facade.getColor());
        }
    }



}
