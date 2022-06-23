package com.apushkin.ssure.csv.model;

import com.opencsv.bean.CsvBindByName;

/**
 * This class represents a street name for parsing csv purposes
 * according to openCsv documentation. The column name should be
 * parameterized in the external configuration in the future releases
 * */
public class StreetName {

    @CsvBindByName(column = "full_street_name", required = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
