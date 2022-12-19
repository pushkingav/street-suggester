package com.apushkin.ssure.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.TermvectorsResponse;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.elasticsearch.core.termvectors.TermVector;
import co.elastic.clients.util.ObjectBuilder;
import com.apushkin.ssure.search.model.ElasticStoreAddress;
import com.apushkin.ssure.search.model.ElasticStreetName;
import com.apushkin.ssure.search.model.SearchField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Search for terms and suggestions in Elasticsearch instance.
 * This version of the service is compatible and was designed for Elasticsearch 8.2.3
 */

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

    public List<String> searchMultiMatch(String searchTerm) throws IOException {
        SearchResponse<ElasticStoreAddress> response = client.search(s -> s
                .index("addresses")
                .query(q -> q
                        .multiMatch(t -> t
                                .fields(List.of("city", "businessName", "addressLine", "postalCode"))
                                .type(TextQueryType.CrossFields)
                                .query(searchTerm)
                        )
                ), ElasticStoreAddress.class);


        return convertResponse(response);
    }

    public SearchResponse<ElasticStoreAddress> searchMultipleFields(String pharmaName, String address, String zip,
                                                                    String city, String state, boolean isPhrase) throws IOException {
        List<Query> queries = buildQueries(pharmaName, address, zip, city, state, isPhrase);
        BoolQuery.Builder builder = QueryBuilders.bool().must(queries);
        SearchResponse<ElasticStoreAddress> response = client.search(s -> s
                        .index("addresses")
                        .query(builder.build()._toQuery()),
                ElasticStoreAddress.class
        );
        return response;
    }

    private List<Query> buildQueries(String pharmaName, String address, String zip, String city, String state,
                                     boolean isPhrase) {
        List<Query> queries = new ArrayList<>();
        if (!StringUtils.isBlank(pharmaName)) {
            if (isPhrase) {
                queries.add(createMatchPhraseQuery(SearchField.BUSINESS_NAME, pharmaName));
            } else {
                queries.add(createMatchQuery(SearchField.BUSINESS_NAME, pharmaName));
            }
        }
        if (!StringUtils.isBlank(address)) {
            queries.add(createMatchQuery(SearchField.ADDRESS, address));
        }
        if (!StringUtils.isBlank(city)) {
            queries.add(createMatchQuery(SearchField.CITY, city));
        }
        if (!StringUtils.isBlank(state)) {
            queries.add(createMatchQuery(SearchField.STATE, state));
        }
        if (!StringUtils.isBlank(zip)) {
            queries.add(createMatchQuery(SearchField.ZIP, zip));
        }
        return queries;
    }

    private Query createMatchQuery(SearchField field, String query) {
        MatchQuery result;
        switch (field) {
            case BUSINESS_NAME, ADDRESS, CITY -> result = MatchQuery.of(m -> m
                    .field(field.toString())
                    .query(query)
                    .fuzziness("AUTO:3,6")
                    .analyzer("english"));
            default -> result = MatchQuery.of(m -> m
                    .field(field.toString())
                    .query(query));
        }
        return result._toQuery();
    }

    private Query createMatchPhraseQuery(SearchField field, String query) {
        MatchPhraseQuery result;
        switch (field) {
            case BUSINESS_NAME, ADDRESS, CITY -> result = MatchPhraseQuery.of(m -> m
                    .field(field.toString())
                    .query(query)
                    .analyzer("english"));
            default -> result = MatchPhraseQuery.of(m -> m
                    .field(field.toString())
                    .query(query));
        }
        return result._toQuery();
    }

    public List<String> convertResponse(SearchResponse<ElasticStoreAddress> response) {
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        for (Hit<ElasticStoreAddress> hit : hits) {
            ElasticStoreAddress storeAddress = hit.source();
            logger.info("Found street storeAddress {} {} {} {}, score {} ", storeAddress.getPostalCode(),
                    storeAddress.getBusinessName(), storeAddress.getAddressLine(), storeAddress.getCity(), hit.score());
        }
        return hits
                .stream()
                .map(hit -> String.join("||", String.format("(%.2f)", hit.score()), hit.source().getPostalCode(),
                        hit.source().getBusinessName(), hit.source().getCity(), hit.source().getAddressLine(),
                        hit.source().getState()))
                .toList();
    }

    public List<String> getAllTokens() throws IOException {
        MatchAllQuery matchAllQuery = MatchAllQuery
                .of(maq -> (ObjectBuilder<MatchAllQuery>) maq.build())._toQuery().matchAll();

        /*TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }*/

       /* List<Hit<ElasticStreetName>> hits = response.hits().hits();
        for (Hit<ElasticStreetName> hit : hits) {
            ElasticStreetName name = hit.source();
            logger.info("Found street name " + name.getName() + ", score " + hit.score());
        }*/


        TermvectorsResponse termvectors = client.termvectors(builder -> builder
                .index("addresses")
                .fields(Collections.singletonList(SearchField.BUSINESS_NAME.toString()))
                .id("rKrJHIUBKD45lZdnW0KA")
        );
        Map<String, TermVector> stringTermVectorMap = termvectors.termVectors();
        Set<String> terms = stringTermVectorMap.get("businessName").terms().keySet();
        return new ArrayList<>(terms);
    }
}
