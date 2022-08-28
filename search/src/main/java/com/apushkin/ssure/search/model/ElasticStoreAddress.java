package com.apushkin.ssure.search.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;

@Document(indexName = "addresses")
public class ElasticStoreAddress {
    @Id
    private String id;

    private String city;

    private String addressLine;

    private String businessName;

    @JsonIgnore
    private String _class;

    public ElasticStoreAddress() {
    }

    public ElasticStoreAddress(String city, String addressLine, String businessName) {
        this.city = city;
        this.addressLine = addressLine;
        this.businessName = businessName;
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

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElasticStoreAddress that = (ElasticStoreAddress) o;
        return Objects.equals(id, that.id)
                && Objects.equals(city, that.city)
                && Objects.equals(addressLine, that.addressLine)
                && Objects.equals(businessName, that.businessName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, addressLine, businessName);
    }
}
