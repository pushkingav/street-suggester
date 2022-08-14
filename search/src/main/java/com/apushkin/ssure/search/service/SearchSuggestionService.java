package com.apushkin.ssure.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.*;
import com.apushkin.ssure.search.model.ElasticStoreAddress;
import com.apushkin.ssure.search.model.ElasticStreetName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public List<TermSuggestOption> searchWithSuggestion(String searchTerm) throws IOException {
        SearchResponse<ElasticStoreAddress> response = client.search(searchRequestBuilder -> searchRequestBuilder
                        .index("addresses")
                        .suggest(suggestBuilder -> suggestBuilder
                                .text(searchTerm)
                                .suggesters("term-suggester", fieldSuggesterBuilder -> fieldSuggesterBuilder
                                        .term(termBuilder -> termBuilder.field("addressLine")
//                                                .lowercaseTerms(true)
                                        ))),
                ElasticStoreAddress.class);
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;
        List<TermSuggestOption> options = Collections.emptyList();
        if (total.value() == 0) {
            Map<String, List<Suggestion<ElasticStoreAddress>>> suggest = response.suggest();
            List<Suggestion<ElasticStoreAddress>> suggestions = suggest.get("term-suggester");
            Suggestion<ElasticStoreAddress> elasticStoreAddressSuggestion = suggestions.get(0);
            return elasticStoreAddressSuggestion.term().options();
        }
        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }

        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        for (Hit<ElasticStoreAddress> hit : hits) {
            ElasticStoreAddress name = hit.source();
            logger.info("Found street name " + name.getAddressLine() + ", score " + hit.score());
        }
        return options;
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
