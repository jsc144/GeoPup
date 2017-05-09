package com.example.groupproject.Model;

/**
 * Created by Joe on 5/8/2017.
 */

public class Constants {
    public static final int PERIOD = 3 * 60000; //3 minutes(all constants designed for 10 minutes)
    public static final int MY_PERMISSIONS_REQUEST = 1;
    public static final long MAX_HEALTH = 1008;//1 week
    public static final long MAX_HUNGER = 1008;//1 week
    public static final long MAX_JOY = 84; //7 hours
    public static final long POINT_INCREMENT = 10;
    public static final long HEALTH_INCREMENT = -1;
    public static final long HUNGER_INCREMENT = -1;
    public static final long JOY_INCREMENT = -1;
    public static final long FOOD_INCREMENT=257;// 1/4
    public static final long TREAT_INCREMENT=21;// 1/4
    public static final long FOOD_COST=60; // takes 1 hour to earn, 4 needed to fill up
    public static final long TREAT_COST=120;// takes 2 hours to earn, 4 needed to be joy-filled
    public static final long MEDICINE_COST = 180;// takes 3 hours to earn, 1 needed to heal
    public static final String DEFAULT_NAME = "";
}
