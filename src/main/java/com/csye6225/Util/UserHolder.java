package com.csye6225.Util;

import com.csye6225.POJO.User;


public class UserHolder {
    private static final ThreadLocal<User> tl = new ThreadLocal<>();
    public static void saveUser(User user) {
        tl.set(user);
    }
    public static User getUser(){
        return tl.get();
    }
    public static void deleteUser(){
        tl.remove();
    }
}
