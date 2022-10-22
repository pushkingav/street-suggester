package com.apushkin.ssure.search.model;

public enum SearchField {
    BUSINESS_NAME("businessName"),
    ADDRESS("addressLine"),
    CITY("city"),
    STATE("state"),
    ZIP("postalCode");

    private String elasticFieldName;

    SearchField(String elasticFieldName) {
        this.elasticFieldName = elasticFieldName;
    }

    @Override
    public String toString() {
        return this.elasticFieldName;
    }
}
