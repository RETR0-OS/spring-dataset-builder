package com.aaditya.honors.datasetBuilder.Repositories;

import com.aaditya.honors.datasetBuilder.Models.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepo extends JpaRepository<Dataset, Long> {

}
