package com.example.groupproject.Model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by hasit on 5/4/2017.
 */

public class Zone implements Serializable{

    private String name, id;
    private int hours, zoneType;
    private double start_lat, start_long, end_lat, end_long;

    public Zone(String name, int hours, int zoneType, double start_lat,
                double start_long, double end_lat, double end_long){
        this.name = name;
        this.hours = hours;
        //0 if go 1 if nogo
        this.zoneType = zoneType;
        this.start_lat = start_lat;
        this.start_long = start_long;
        this.end_lat = end_lat;
        this.end_long = end_long;
        this.id = UUID.randomUUID().toString();;
    }

    public void setID(String id){
        this.id = id;
    }

    public String  getID(){
        return id;
    }


    public String getName(){
        return name;
    }

    public int getHours(){
        return hours;
    }

    public int getZoneType(){
        return zoneType;
    }

    public double getStart_lat(){
        return start_lat;
    }

    public double getStart_long(){
        return start_long;
    }

    public double getEnd_lat(){
        return end_lat;
    }

    public double getEnd_long(){
        return end_long;
    }
}
