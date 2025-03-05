package com.aaditya.honors.datasetBuilder.Models;


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