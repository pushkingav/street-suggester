package com.apushkin.ssure.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.stereotype.Service;

@Service
public class SearchSuggestionService {
    private final ElasticsearchClient client;

    public SearchSuggestionService(ElasticsearchClient client) {
        this.client = client;
    }

    public void searchWithSuggestion(String searchTerm) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("name", searchTerm));
        SuggestionBuilder<TermSuggestionBuilder> suggestionBuilder = new TermSuggestionBuilder("name")
                .text(searchTerm);
        SuggestBuilder suggestion = new SuggestBuilder().addSuggestion("term-suggester", suggestionBuilder);
//        SearchRequestBuilder requestBuilder = client.
    }
}
