package com.example.agadimaganda.findyourownbarber.Object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aga diMaganda on 7.02.2018.
 */

public class Barber {

    private int id;
    private String barberName;
    private String address;
    private Double latitude;
    private Double longitude;
    private ArrayList<String> HCStyleList = new ArrayList<>();
    private ArrayList<Double> HCStyleCostList = new ArrayList<>();
    private ArrayList<Double> HCStyleRateList = new ArrayList<>();
    private Double barberRate;
    private String city;
    private List<Comment> commentList;

    public Barber(Object tag) {

    }

    public Barber(){

    }

    public Barber(String barberName, Double latitude, Double longitude, String city, List<Comment> commentList){

        this.barberName = barberName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.commentList = commentList;
    }


    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getBarberName(){
        return barberName;
    }
    public void setBarberName(String barberName){
        this.barberName = barberName;
    }

    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public Double getLongitude(){
        return longitude;
    }
    public void setLongitude(Double longitude){
        this.longitude = longitude;
    }

    public Double getLatitude(){
        return latitude;
    }
    public void setLatitude(Double latitude){
        this.latitude = latitude;
    }

    public ArrayList<String> getHCStyleList(){
        return HCStyleList;
    }
    public void setHCStyleList(ArrayList<String> HCStyleList){
        this.HCStyleList = HCStyleList;
    }

    public ArrayList<Double> getHCStyleCostList(){
        return HCStyleCostList;
    }
    public void setHCStyleCostList(ArrayList<Double> HCStyleCostList){
        this.HCStyleCostList = HCStyleCostList;
    }

    public ArrayList<Double> getHCStyleRateList(){
        return HCStyleRateList;
    }
    public void setHCStyleRateList(ArrayList<Double> HCStyleRateList){
        this.HCStyleRateList = HCStyleRateList;
    }

    public Double getBarberRate(){
        return barberRate;
    }
    public void setBarberRate(Double barberRate){
        this.barberRate = barberRate;
    }

    public String getCity(){
        return city;
    }
    public void setCity(String city){
        this.city = city;
    }

    public List<Comment> getCommentList(){
        return commentList;
    }
    public void setCommentList(List<Comment> commentList){
        this.commentList = commentList;
    }

}
