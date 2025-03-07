package com.aaditya.honors.datasetBuilder.Services;

import com.aaditya.honors.datasetBuilder.Models.Dataset;
import com.aaditya.honors.datasetBuilder.Repositories.DatasetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasetService {

    @Autowired
    private DatasetRepo datasetRepo;

    public void add_dataset(Dataset dataset) {
        datasetRepo.save(dataset);
    }

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
}
