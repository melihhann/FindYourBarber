package com.example.agadimaganda.findyourownbarber.Object;

/**
 * Created by Aga diMaganda on 18.04.2018.
 */

public class Upload {

    private String imageUrl;
    private String imageId;
    private String lastPathSegment;
    private String userId;


    public Upload(){

    }

    public Upload(String imageUrl){
        imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return  imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageId(){
        return imageId;
    }

    public void setImageId(String imageId){
        this.imageId = imageId;
    }

    public String getLastPathSegment(){
        return lastPathSegment;
    }

    public void setLastPathSegment(String lastPathSegment){
        this.lastPathSegment = lastPathSegment;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

}
