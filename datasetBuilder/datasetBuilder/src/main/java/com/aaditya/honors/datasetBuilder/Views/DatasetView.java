package com.aaditya.honors.datasetBuilder.Views;


//A view in SpringBoot is used to return a subset of the dataset fields
//This view controls when the entire dataset is to be returned and when only the fields required in a dataCard are to be returned
public class DatasetView {

    //This view is used to return a only the fields required in a dataCard
    public interface DatasetCard{}

    //This view is used to return the entire dataset
    public interface DatasetFull extends DatasetCard{}
}
