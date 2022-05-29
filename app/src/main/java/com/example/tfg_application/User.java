package com.example.tfg_application;

public class User{
    public String firstName;
    private String nickName;
    public String email;

    public User() {
    }

    public User (String firstName, String nickName, String email) {
        this.firstName = firstName;
        this.nickName = nickName;
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }
}
