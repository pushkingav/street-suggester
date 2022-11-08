package com.apushkin.ssure.csv.model;

import com.opencsv.bean.CsvBindByName;

/**
 * This class represents the address of a store for parsing csv purposes
 * according to openCsv documentation.
 * */
public class StoreAddress {

    @CsvBindByName(column = "city", required = true)
    private String city;

    @CsvBindByName(column = "standardizedAddressLine1")
    private String addressLine;

    @CsvBindByName(column = "businessName", required = true)
    private String businessName;

    @CsvBindByName(column = "postalCode")
    private String postalCode;

    @CsvBindByName(column = "standardizedStateProvince")
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
