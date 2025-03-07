package com.aaditya.honors.datasetBuilder.Controllers;

import com.aaditya.honors.datasetBuilder.Models.DataPoint;
import com.aaditya.honors.datasetBuilder.Models.Dataset;
import com.aaditya.honors.datasetBuilder.Models.DatasetTransferObj;
import com.aaditya.honors.datasetBuilder.Services.DatasetService;
import com.aaditya.honors.datasetBuilder.Views.DatasetView;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/api/v1/datasets")
public class DatasetController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String alpha_extractor = "rgba\\(\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*([\\d\\.]+)\\s*\\)";
    private final Pattern pattern = Pattern.compile(alpha_extractor);

    @Autowired
    DatasetService datasetService;

    @PostMapping("/add/new/")
    public ResponseEntity<Map<String, String>> create_dataset(@RequestBody DatasetTransferObj request) {
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
                String opacity = matcher.group(1);
                double value = (Double.parseDouble(opacity) * (scales.get(3).get(1) - scales.get(3).get(0))) + scales.get(3).get(0);
                data_line.append(",").append(opacity);
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

        datasetService.add_dataset(newDataset);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Dataset added successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/list/all/")
    @JsonView(DatasetView.DatasetCard.class)
    public ResponseEntity<Map<String, List<Dataset>>> get_datasets(){
        Map<String, List<Dataset>> response = new HashMap<>();
        response.put("datasets", datasetService.get_datasets());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{id}/")
    @JsonView(DatasetView.DatasetFull.class)
    public ResponseEntity<Map<String, Dataset>> get_dataset(@PathVariable long id){
        Map<String, Dataset> response = new HashMap<>();
        Dataset dataset = datasetService.get_dataset(id);
        if(dataset == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        response.put("dataset", dataset);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete_dataset(@PathVariable long id){
        String result = datasetService.delete_dataset(id);
        if(result == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Dataset deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
