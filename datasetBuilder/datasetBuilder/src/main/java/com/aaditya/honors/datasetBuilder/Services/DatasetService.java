package com.aaditya.honors.datasetBuilder.Services;

import com.aaditya.honors.datasetBuilder.Models.Dataset;
import com.aaditya.honors.datasetBuilder.Repositories.DatasetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatasetService {

    @Autowired
    private DatasetRepo datasetRepo;

    public void add_dataset(Dataset dataset) {
        datasetRepo.save(dataset);
    }

}
