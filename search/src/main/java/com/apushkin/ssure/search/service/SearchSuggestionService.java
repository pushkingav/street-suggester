package com.apushkin.ssure.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.apushkin.ssure.search.model.ElasticStreetName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Search for terms and suggestions in Elasticsearch instance.
 * This version of the service is compatible and was designed for Elasticsearch 8.2.3
 */

//TODO - add service version compatible with AWS Elasticsearch (7.x.?) and use @ConditionalOnProperty
//TODO - use properties to determine where the app run: on-premises (using Docker?) or in AWS
@Service
public class SearchSuggestionService {
    private static final Logger logger = LoggerFactory.getLogger(SearchSuggestionService.class);
    private final ElasticsearchClient client;

    public SearchSuggestionService(ElasticsearchClient client) {
        this.client = client;
    }

    public void searchWithSuggestion(String searchTerm) throws IOException {
        SearchResponse<ElasticStreetName> response = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("addresses")
                        .suggest(suggestBuilder -> suggestBuilder
                                .text(searchTerm)
                                .suggesters("term-suggester", fieldSuggesterBuilder -> fieldSuggesterBuilder
                                        .term(termBuilder -> termBuilder.field("name")
                                                .lowercaseTerms(true)
                                        ))),
                ElasticStreetName.class);

    }

    public void searchInsideElastic(String searchTerm) throws IOException {
        SearchResponse<ElasticStreetName> response = client.search(s -> s
                .index("streets")
                .query(q -> q
                        .match(t -> t
                                .field("name")
                                .query(searchTerm)
                        )
                ), ElasticStreetName.class);
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }

        List<Hit<ElasticStreetName>> hits = response.hits().hits();
        for (Hit<ElasticStreetName> hit : hits) {
            ElasticStreetName name = hit.source();
            logger.info("Found street name " + name.getName() + ", score " + hit.score());
        }
    }
}
