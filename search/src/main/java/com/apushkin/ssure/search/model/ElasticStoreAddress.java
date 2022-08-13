package com.apushkin.ssure.search.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;

@Document(indexName = "addresses")
public class ElasticStoreAddress {
    @Id
    private String id;

    private String city;

    private String addressLine;

    public ElasticStoreAddress() {
    }

    public ElasticStoreAddress(String city, String addressLine) {
        this.city = city;
        this.addressLine = addressLine;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElasticStoreAddress)) return false;
        ElasticStoreAddress that = (ElasticStoreAddress) o;
        return Objects.equals(id, that.id)
                && Objects.equals(city, that.city)
                && Objects.equals(addressLine, that.addressLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, addressLine);
    }
}
