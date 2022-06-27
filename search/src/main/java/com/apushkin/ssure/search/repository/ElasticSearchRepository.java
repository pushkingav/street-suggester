package com.apushkin.ssure.search.repository;

import com.apushkin.ssure.search.model.ElasticStreetName;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticStreetName, String> {

}
