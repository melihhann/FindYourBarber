package com.example.agadimaganda.findyourownbarber;

/**
 * Created by Aga diMaganda on 18.04.2018.
 */

public class Upload {

    private String imageUrl;

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
}
