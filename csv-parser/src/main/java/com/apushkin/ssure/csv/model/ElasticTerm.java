package com.apushkin.ssure.csv.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;

@Document(indexName = "addresses")
//@Setting(settingPath = "/text_analyzer.json")
public class ElasticTerm {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "english")
    private String textTerm;

    @Field(type = FieldType.Keyword)
    private String keywordTerm;

    public ElasticTerm() {
    }

    public ElasticTerm(String id, String textTerm, String keywordTerm) {
        this.id = id;
        this.textTerm = textTerm;
        this.keywordTerm = keywordTerm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextTerm() {
        return textTerm;
    }

    public void setTextTerm(String textTerm) {
        this.textTerm = textTerm;
    }

    public String getKeywordTerm() {
        return keywordTerm;
    }

    public void setKeywordTerm(String keywordTerm) {
        this.keywordTerm = keywordTerm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElasticTerm that = (ElasticTerm) o;
        return Objects.equals(id, that.id)
                && Objects.equals(textTerm, that.textTerm)
                && Objects.equals(keywordTerm, that.keywordTerm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, textTerm, keywordTerm);
    }
}
