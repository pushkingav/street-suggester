package com.apushkin.ssure.csv.repository;

import com.apushkin.ssure.csv.model.ElasticStreetName;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticsearchIngestRepository extends ElasticsearchRepository<ElasticStreetName, String> {

}
