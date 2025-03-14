package com.aaditya.honors.datasetBuilder.Repositories;

import com.aaditya.honors.datasetBuilder.Models.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

//This interface defines a repository for the Dataset entity
//It extends JpaRepository, which provides basic CRUD operations
public interface DatasetRepo extends JpaRepository<Dataset, Long> {

}
