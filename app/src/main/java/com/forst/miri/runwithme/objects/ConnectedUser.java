package com.forst.miri.runwithme.objects;

/**
 * Created by chagithazani on 8/30/17.
 */

public class ConnectedUser extends User {
    private static User ourInstance;

    public static User getInstance() {
        return ourInstance;
    }

    private ConnectedUser() {    }

    public static void setUser(User user){
        ourInstance = user;
    }

    public static int getLessonNum() {
        return getInstance().getPracticeNum();
    }

}
