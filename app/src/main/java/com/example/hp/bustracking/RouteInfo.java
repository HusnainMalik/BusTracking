package com.example.hp.bustracking;

import java.io.Serializable;

public class RouteInfo implements Serializable{

    int bus_id;
    String bus_name;
    String bus_no;
    String driver_id;
    int bus_status;
    long locationUpdateTime;
    double latitude;
    double longitude;
}
