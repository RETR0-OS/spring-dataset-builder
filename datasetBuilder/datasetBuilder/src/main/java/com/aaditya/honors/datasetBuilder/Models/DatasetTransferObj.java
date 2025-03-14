package com.aaditya.honors.datasetBuilder.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import com.aaditya.honors.datasetBuilder.Models.DataPoint;
import java.util.HashMap;



//This intermediate class is used to transfer data between the frontend and backend
//This class is modelled after the JSON object sent from the frontend to allow processing of the data before it is sent to the DB.
public class DatasetTransferObj {

    @JsonProperty("dataset_title")
    private String dataset_title;

    @JsonProperty("dataset_description")
    private String dataset_description;

    @JsonProperty("categorical_column_name")
    private String categorical_col_name;

    @JsonProperty("point_class")
    private ArrayList<String> categorical_col_values;

    @JsonProperty("data_points")
    private ArrayList<DataPoint> data_point;

    @JsonProperty("data_bg_color")
    private ArrayList<String> bg_color;

    @JsonProperty("point_radius")
    private ArrayList<Double> point_radius;

    @JsonProperty("numerical_column_names")
    private ArrayList<String> numerical_col_names;

    @JsonProperty("scales")
    private ArrayList<ArrayList<Double>> scales;

    @JsonProperty("categorical_keys")
    HashMap<String, String> categorical_keys = new HashMap<>();

    public ArrayList<ArrayList<Double>> getScales() {
        return scales;
    }

    public void setScales(ArrayList<ArrayList<Double>> scales) {
        this.scales = scales;
    }

    public HashMap<String, String> getCategorical_keys() {
        return categorical_keys;
    }

    public void setCategorical_keys(HashMap<String, String> categorical_keys) {
        this.categorical_keys = categorical_keys;
    }

    public ArrayList<String> getCategorical_col_values() {
        return categorical_col_values;
    }

    public void setCategorical_col_values(ArrayList<String> categorical_col_values) {
        this.categorical_col_values = categorical_col_values;
    }

    public String getCategorical_col_name() {
        return categorical_col_name;
    }

    public void setCategorical_col_name(String categorical_col_name) {
        this.categorical_col_name = categorical_col_name;
    }

    public ArrayList<String> getBg_color() {
        return bg_color;
    }

    public void setBg_color(ArrayList<String> bg_color) {
        this.bg_color = bg_color;
    }

    public ArrayList<DataPoint> getData_point() {
        return data_point;
    }

    public void setData_point(ArrayList<DataPoint> data_point) {
        this.data_point = data_point;
    }

    public ArrayList<Double> getPoint_radius() {
        return point_radius;
    }

    public void setPoint_radius(ArrayList<Double> point_radius) {
        this.point_radius = point_radius;
    }

    public ArrayList<String> getNumerical_col_names() {
        return numerical_col_names;
    }

    public void setNumerical_col_names(ArrayList<String> numerical_col_names) {
        this.numerical_col_names = numerical_col_names;
    }

    public String getDataset_description() {
        return dataset_description;
    }

    public void setDataset_description(String dataset_description) {
        this.dataset_description = dataset_description;
    }

    public String getDataset_title() {
        return dataset_title;
    }

    public void setDataset_title(String dataset_title) {
        this.dataset_title = dataset_title;
    }
}
