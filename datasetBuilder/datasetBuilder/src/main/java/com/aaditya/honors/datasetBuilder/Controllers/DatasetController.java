package com.aaditya.honors.datasetBuilder.Controllers;

import com.aaditya.honors.datasetBuilder.Models.DataPoint;
import com.aaditya.honors.datasetBuilder.Models.Dataset;
import com.aaditya.honors.datasetBuilder.Models.DatasetTransferObj;
import com.aaditya.honors.datasetBuilder.Services.DatasetService;
import com.aaditya.honors.datasetBuilder.Views.DatasetView;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/api/v1/datasets")
public class DatasetController {
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    DatasetService datasetService;

    @PostMapping("/add/new/")
    public ResponseEntity<Map<String, String>> create_dataset(@RequestBody DatasetTransferObj request) {

        String result = datasetService.add_dataset(request);
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
        if(result == null){
            System.out.println("not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println("deleted");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Dataset deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{id}/download/")
    public ResponseEntity<ByteArrayResource> download_dataset(@PathVariable long id){
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"data.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(datasetService.download_dataset(id));
    }


}
