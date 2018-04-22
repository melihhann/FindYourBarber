package com.example.agadimaganda.findyourownbarber;

/**
 * Created by Aga diMaganda on 21.04.2018.
 */

public class Like {

    private String userId;


    public Like(String userId) {
        this.userId = userId;
    }

    public Like() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public String toString() {
        return "Like{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
