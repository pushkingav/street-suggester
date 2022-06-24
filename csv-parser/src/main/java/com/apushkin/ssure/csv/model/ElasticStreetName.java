package com.apushkin.ssure.csv.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * This class represents a street name for parsing csv purposes
 * according to openCsv documentation. The column name should be
 * parameterized in the external configuration in the future releases
 * */
@Document(indexName = "streets")
public class ElasticStreetName {
    @Id
    private String id;

    private String name;

    public ElasticStreetName() {
    }

    public ElasticStreetName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
