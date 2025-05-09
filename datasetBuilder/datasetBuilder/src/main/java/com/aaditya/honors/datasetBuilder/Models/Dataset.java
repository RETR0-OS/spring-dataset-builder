package com.aaditya.honors.datasetBuilder.Models;
import com.aaditya.honors.datasetBuilder.Views.DatasetView;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;



//This class models the Dataset object for the database.
//It declares the DB schema and the fields for the dataset.
@Entity
@Table(name = "datasets")
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DatasetView.DatasetCard.class)
    private long id;

    @JsonView(DatasetView.DatasetCard.class)
    private String name;
    @JsonView(DatasetView.DatasetCard.class)
    private String description;

    @JsonView(DatasetView.DatasetFull.class)
    private String headers;

    @Column(columnDefinition = "TEXT")
    @JsonView(DatasetView.DatasetFull.class)
    private String data;

    public Dataset() {

    }

    public Dataset(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Dataset(long id, String name, String description, String headers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.headers = headers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public String toString() {
        return "Dataset{" + "id=" + id + ", name=" + name + ", description=" + description + ", headers=" + headers + ", data=" + data.length() + '}';
    }
}
