package com.example.agadimaganda.findyourownbarber;

import java.util.ArrayList;

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

}
