package com.apushkin.ssure.csv.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;

@Document(indexName = "addresses")
public class ElasticStoreAddress {
    @Id
    private String id;

    private String city;

    private String addressLine;

    @Field(type = FieldType.Text, analyzer = "english")
    private String businessName;

    private String postalCode;

    private String state;

    public ElasticStoreAddress() {
    }

    public ElasticStoreAddress(String city, String addressLine, String businessName, String postalCode, String state) {
        this.city = city;
        this.addressLine = addressLine;
        this.businessName = businessName;
        this.postalCode = postalCode;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElasticStoreAddress that = (ElasticStoreAddress) o;
        return Objects.equals(id, that.id)
                && Objects.equals(city, that.city)
                && Objects.equals(addressLine, that.addressLine)
                && Objects.equals(businessName, that.businessName)
                && Objects.equals(postalCode, that.postalCode)
                && Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, addressLine, businessName, postalCode, state);
    }
}
