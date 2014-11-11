package com.camlab.alexslosquaraapp;

/**
 * Created by alex on 10.11.2014.
 */
public class Place {
    private final String name;
    private final String category;
    private final String distance;
    private final String address;

    public Place(String name, String category, String distance, String address) {
        this.name = name;
        this.category = category;
        this.distance = distance;
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDistance() {
        return distance;
    }

    public String getAddress() {
        return address;
    }
}

