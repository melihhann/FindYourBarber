package com.example.agadimaganda.findyourownbarber.Object;

import com.example.agadimaganda.findyourownbarber.Object.Comment;

/**
 * Created by Aga diMaganda on 21.04.2018.
 */

public class Like {

    private String userId;
    private Comment comment;


    public Like(String userId, Comment comment) {
        this.userId = userId;
        this.comment = comment;
    }

    public Like() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Comment getComment(){
        return comment;
    }

    public void setComment(Comment comment){
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Like{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
