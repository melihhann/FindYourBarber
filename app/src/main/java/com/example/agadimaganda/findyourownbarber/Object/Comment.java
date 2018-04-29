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

    public  Comment(){

    }

    public Comment(String comment, Long likes, String dateCreated, String barberName) {
        this.comment = comment;
        this.likes = likes;
        this.dateCreated = dateCreated;
        this.barberName = barberName;
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

}
