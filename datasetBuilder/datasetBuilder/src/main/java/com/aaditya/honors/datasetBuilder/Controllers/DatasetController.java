package com.aaditya.honors.datasetBuilder.Controllers;


import com.aaditya.honors.datasetBuilder.Models.DataPoint;
import com.aaditya.honors.datasetBuilder.Models.Dataset;
import com.aaditya.honors.datasetBuilder.Models.DatasetTransferObj;
import com.aaditya.honors.datasetBuilder.Services.DatasetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;

@RestController
@RequestMapping("api/v1/datasets")
public class DatasetController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String alpha_extractor = "rgba\\(\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*([\\d\\.]+)\\s*\\)";
    private final Pattern pattern = Pattern.compile(alpha_extractor);

    @Autowired
    DatasetService datasetService;

    @PostMapping("/add/new/")
    public String create_dataset(@RequestBody DatasetTransferObj request) {
        Dataset newDataset = new Dataset();

        newDataset.setName(request.getDataset_title());
        newDataset.setDescription(request.getDataset_description());

        StringBuilder headers = new StringBuilder();
        ArrayList<String> numerical_col_names = request.getNumerical_col_names();
        String categorical_col_name = request.getCategorical_col_name();
        ArrayList<String> categorical_col_values = request.getCategorical_col_values();
        ArrayList<String> bg_color = request.getBg_color();
        ArrayList<Double> point_radius = request.getPoint_radius();
        ArrayList<DataPoint> data_points = request.getData_point();
        StringBuilder dataset_data = new StringBuilder();
        ArrayList<ArrayList<Double>> scales = request.getScales();
        HashMap<String, String> categorical_keys = request.getCategorical_keys();

        for (String s: numerical_col_names) {
            headers.append(s).append(",");
        }
        if(categorical_col_name != null) {
            headers.append(categorical_col_name);
        }

        for (int i = 0; i < data_points.size(); i++) {
            StringBuilder data_line = new StringBuilder();
            DataPoint dataPoint = data_points.get(i);
            data_line.append(dataPoint.getData());
            if(numerical_col_names.size() >= 3) {
                Double raw_radius = point_radius.get(i);
                ArrayList<Double> scale = scales.get(2);
                //point_radius = 5 + ((numeric_1.value * 9.5) / (numeric_1.max - numeric_1.min)); //Sets point radius to be between 5 and 100.
                double value = (raw_radius-5) * ((scale.get(1) - scale.get(0)) / 9.5);
                data_line.append(",").append(value);
            }
            if (numerical_col_names.size() >= 4) {
                String bg_color_line = bg_color.get(i);
                Matcher matcher = pattern.matcher(bg_color_line);
                String opacity = matcher.group(1);
                //opacity = (numeric_2.value - numeric_2.min) / (numeric_2.max - numeric_2.min); //Sets opacity to be between 0 and 1.
                double value = (Double.parseDouble(opacity) * (scales.get(3).get(1) - scales.get(3).get(0))) + scales.get(3).get(0);
                data_line.append(",").append(opacity);
            }
            if (categorical_col_name != null) {
                String shape = categorical_col_values.get(i);
                System.out.println(shape);
                String class_name = categorical_keys.get(shape);
                data_line.append(",").append(class_name);
            }
            data_line.append("\n");
            dataset_data.append(data_line);
        }

        newDataset.setHeaders(headers.toString());
        newDataset.setData(dataset_data.toString());

        datasetService.add_dataset(newDataset);

        return "Dataset created successfully";


    }


}
