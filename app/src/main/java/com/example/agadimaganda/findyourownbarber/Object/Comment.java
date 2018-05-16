package com.example.agadimaganda.findyourownbarber.Object;

import java.util.List;

/**
 * Created by Aga diMaganda on 21.04.2018.
 */

public class Comment {

    private String comment;
    private Long likes;
    private String dateCreated;
    private String barberName;
    private String userId;
    private String commentId;
    private String likePosNeg;

    public  Comment(){

    }

    public Comment(String comment, Long likes, String dateCreated, String barberName, String userId, String commentId, String likePosNeg) {
        this.comment = comment;
        this.likes = likes;
        this.dateCreated = dateCreated;
        this.barberName = barberName;
        this.userId = userId;
        this.commentId = commentId;
        this.likePosNeg = likePosNeg;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBarberName(){
        return barberName;
    }

    public void setBarberName(String barberName){
        this.barberName = barberName;
    }

    public String getUserId(){
        return  userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getCommentId(){
        return commentId;
    }

    public void setCommentId(String commentId){
        this.commentId = commentId;
    }

    public String getLikePosNeg(){
        return likePosNeg;
    }

    public void setLikePosNeg(String likePosNeg){
        this.likePosNeg = likePosNeg;
    }

}
