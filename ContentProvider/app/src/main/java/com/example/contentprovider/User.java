package com.example.contentprovider;

public class User {
    public int userId;
    public String userName;
    public boolean isMale;

    public User(){

    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isMale() {
        return isMale;
    }
    public String toString(){
        return this.userName+" "+this.userId;
    }
}
