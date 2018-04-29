package com.example.agadimaganda.findyourownbarber.Object;

import com.example.agadimaganda.findyourownbarber.Object.Comment;

import java.util.ArrayList;

/**
 * Created by Aga diMaganda on 21.04.2018.
 */

public class Like {

    private String userId;
    private Long commentLike;


    public Like(String userIdList, Long commentLike) {
        this.userId = userId;
        this.commentLike = commentLike;
    }

    public Like() {
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public Long getCommentLike(){
        return commentLike;
    }

    public void setCommentLike(Long commentLike){
        this.commentLike = commentLike;
    }


    @Override
    public String toString() {
        return "Like{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
