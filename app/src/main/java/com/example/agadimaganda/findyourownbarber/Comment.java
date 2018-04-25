package com.example.agadimaganda.findyourownbarber;

import java.util.List;

/**
 * Created by Aga diMaganda on 21.04.2018.
 */

public class Comment {

    private String comment;
    private List<Like> likes;
    private String dateCreated;

    public  Comment(){

    }

    public Comment(String comment, String userId, List<Like> likes, String dateCreated) {
        this.comment = comment;
        this.likes = likes;
        this.dateCreated = dateCreated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
