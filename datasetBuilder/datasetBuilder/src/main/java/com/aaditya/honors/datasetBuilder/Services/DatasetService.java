package com.aaditya.honors.datasetBuilder.Services;

import com.aaditya.honors.datasetBuilder.Models.DataPoint;
import com.aaditya.honors.datasetBuilder.Models.Dataset;
import com.aaditya.honors.datasetBuilder.Models.DatasetTransferObj;
import com.aaditya.honors.datasetBuilder.Repositories.DatasetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DatasetService {

    private final String alpha_extractor = "rgba\\(\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*([\\d\\.]+)\\s*\\)";
    private final Pattern pattern = Pattern.compile(alpha_extractor);

    @Autowired
    private DatasetRepo datasetRepo;


    public List<Dataset> get_datasets() {
        return datasetRepo.findAll();
    }

    public Dataset get_dataset(long id) {
        return datasetRepo.findById(id).orElse(null);
    }

    public String delete_dataset(long id) {
        if (get_dataset(id) == null) return null;
        datasetRepo.deleteById(id);
        return "Deleted";
    }


    public String add_dataset(DatasetTransferObj request) {
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
                double value = (raw_radius-5) * ((scale.get(1) - scale.get(0)) / 9.5);
                data_line.append(",").append(value);
            }
            if (numerical_col_names.size() >= 4) {
                String bg_color_line = bg_color.get(i);
                Matcher matcher = pattern.matcher(bg_color_line);
                matcher.find();
                String opacity = matcher.group(1);
                double value = (Double.parseDouble(opacity) * (scales.get(3).get(1) - scales.get(3).get(0))) + scales.get(3).get(0);
                data_line.append(",").append(value);
            }
            if (categorical_col_name != null) {
                String shape = categorical_col_values.get(i);
                String class_name = categorical_keys.get(shape);
                data_line.append(",").append(class_name);
            }
            data_line.append("\n");
            dataset_data.append(data_line);
        }

        newDataset.setHeaders(headers.toString());
        newDataset.setData(dataset_data.toString());
        datasetRepo.save(newDataset);
        return newDataset.getData();
    }

    public ByteArrayResource download_dataset(long id) {
        Dataset dataset = get_dataset(id);
        String csv_data = dataset.getData();
        String csv = dataset.getHeaders() + "\n" + csv_data;
        return new ByteArrayResource(csv.getBytes(StandardCharsets.UTF_8));
    }
}
