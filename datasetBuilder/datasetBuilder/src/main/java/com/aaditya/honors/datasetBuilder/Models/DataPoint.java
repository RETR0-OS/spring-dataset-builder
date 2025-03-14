package com.aaditya.honors.datasetBuilder.Models;


//This class models an (x,y) datapoint in the dataset passed by frontend's JSON request, as stored in Chart.JS
public class DataPoint {
    private double x;
    private double y;

    public DataPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String getData() {
        return x+","+y;
    }
}