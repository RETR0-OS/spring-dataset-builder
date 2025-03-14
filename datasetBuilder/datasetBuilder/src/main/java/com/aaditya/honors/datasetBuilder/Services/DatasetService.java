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


//The dataset service is responsible for handling any required processing and business logic for all incoming and outgoing requests
@Service
public class DatasetService {

    //Regex to extract alpha values from RGBA color strings.
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
        Dataset newDataset = new Dataset(); //Declares a new Dataset object to store in the DB
        newDataset.setName(request.getDataset_title());
        newDataset.setDescription(request.getDataset_description());

        //Process the incoming DatasetTransferObj to create a new Dataset

        StringBuilder headers = new StringBuilder(); //header row of the dataset
        ArrayList<String> numerical_col_names = request.getNumerical_col_names(); //numerical column names
        String categorical_col_name = request.getCategorical_col_name(); //categorical column name
        ArrayList<String> categorical_col_values = request.getCategorical_col_values(); //categorical column values in the form of shapes
        ArrayList<String> bg_color = request.getBg_color(); // The list of RGBA values of the background colors of the datapoints
        ArrayList<Double> point_radius = request.getPoint_radius(); //The list of the radii (size) of different data points
        ArrayList<DataPoint> data_points = request.getData_point(); //the (x,y) coordinates of the datapoints themslves, as stored in Chart.JS
        StringBuilder dataset_data = new StringBuilder(); //A string builder to store the dataset rows as strings.
        ArrayList<ArrayList<Double>> scales = request.getScales(); //The list of the min and max values of each numerical column for decoding of the numerical values of the datapoints
        HashMap<String, String> categorical_keys = request.getCategorical_keys(); //The Mapping of the shapes of the categorical values to their actual class labels for storage in the DB

        //build header row and add to dataset object
        for (String s: numerical_col_names) {
            headers.append(s).append(",");
        }
        if(categorical_col_name != null) {
            headers.append(categorical_col_name);
        }
        newDataset.setHeaders(headers.toString());

        //build dataset rows
        for (int i = 0; i < data_points.size(); i++) {
            StringBuilder data_line = new StringBuilder(); //string builder to store a single data row
            DataPoint dataPoint = data_points.get(i); //Select current (x,y) datapoint
            data_line.append(dataPoint.getData()); //add (x,y) datapoint to data row

            //add numerical data to data row
            if(numerical_col_names.size() >= 3) { //If there are more than 2 numerical columns, then the third column would be the radius, which encodes the third dimension of the dataset
                Double raw_radius = point_radius.get(i); //Get the radius of the current datapoint
                ArrayList<Double> scale = scales.get(2); //Get the scale of the third numerical column
                double value = (raw_radius-5) * ((scale.get(1) - scale.get(0)) / 9.5); //convert raw radius to actual value by reversing the operations performed on the frontend.
                data_line.append(",").append(value); //Add to the current row
            }

            if (numerical_col_names.size() >= 4) { //If there are more than 3 numerical columns, then the fourth column would be the opacity, which encodes the fourth dimension of the dataset
                String bg_color_line = bg_color.get(i); //Get the RGBA value of the current datapoint
                Matcher matcher = pattern.matcher(bg_color_line); //Match the opacity value
                matcher.find(); //Extract the opacity
                String opacity = matcher.group(1); //Get the opacity
                double value = (Double.parseDouble(opacity) * (scales.get(3).get(1) - scales.get(3).get(0))) + scales.get(3).get(0); //convert raw opacity to actual value by reversing the operations performed on the frontend
                data_line.append(",").append(value); //Add to the current row
            }

            if (categorical_col_name != null) { //If there is a categorical column, then add the class labels
                String shape = categorical_col_values.get(i); //Get the shape of the current datapoint
                String class_name = categorical_keys.get(shape); //Map the shape to the actual class label
                data_line.append(",").append(class_name); //add the class label to the current row
            }
            data_line.append("\n");
            dataset_data.append(data_line); //add the entire row to the dataset
        }

        newDataset.setData(dataset_data.toString()); //add the dataset to the dataset object
        datasetRepo.save(newDataset); //save the dataset to the DB
        return newDataset.getData();
    }


    public ByteArrayResource download_dataset(long id) {
        /**
         * Downloads the dataset with the given ID as a CSV file.
         *
         * This method retrieves the dataset from the database using the provided ID,
         * constructs a CSV string from the dataset's headers and data, and returns
         * it as a ByteArrayResource.
         *
         * @param id The ID of the dataset to be downloaded.
         * @return A ByteArrayResource containing the CSV representation of the dataset
         *         with UTF-8 encoding.
         */
        Dataset dataset = get_dataset(id); //Fetch the dataset by ID

        //Construct the CSV string by adding the headers and data
        String csv_data = dataset.getData();
        String csv = dataset.getHeaders() + "\n" + csv_data;

        //Return the CSV as a ByteArrayResource for download.
        return new ByteArrayResource(csv.getBytes(StandardCharsets.UTF_8));
    }
}
