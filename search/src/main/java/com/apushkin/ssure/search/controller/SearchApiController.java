package com.apushkin.ssure.search.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.TermSuggestOption;
import com.apushkin.ssure.search.model.ElasticStoreAddress;
import com.apushkin.ssure.search.service.SearchSuggestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SearchApiController {
    private static final Logger log = LoggerFactory.getLogger(SearchApiController.class);

    private final SearchSuggestionService searchSuggestionService;

    public SearchApiController(SearchSuggestionService searchSuggestionService) {
        this.searchSuggestionService = searchSuggestionService;
    }

    @GetMapping(value = "/search")
    public List<String> search(@RequestParam(required = false) String pharmaName,
                               @RequestParam(required = false) String address,
                               @RequestParam(required = false) String zip,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false) String state) throws IOException {
        log.info("Searching for: pharmaName: {}, address: {}, zip: {}, city: {}, state: {}", pharmaName, address, zip,
                city, state);
        List<String> terms = searchSuggestionService.findTerms(pharmaName);
        if (!terms.isEmpty()) {
            List<String> candidates = new ArrayList<>();
            for (String term : terms) {
                if (!term.contains("-") && term.length() > 10) {
                    candidates.addAll(searchSuggestionService.findCandidates(term));
                } else {
                    candidates.add(term);
                }
            }
            if (candidates.isEmpty()) {
                List<String> empty = new ArrayList<>();
                empty.add("Nothing found...");
                return empty;
            } else {
                pharmaName = String.join(" ", candidates);
                log.info("Computed pharmacy name is {}", pharmaName);
            }
        }
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(pharmaName, address, zip, city, state, true);
        if (response.hits().hits().size() == 0) {
            response = searchSuggestionService
                    .searchMultipleFields(pharmaName, address, zip, city, state, false);
        }
        return searchSuggestionService.convertResponse(response);
    }

    @GetMapping(value = "/search/suggest")
    public List<String> searchSuggestions(@RequestParam String searchString) throws IOException {
        List<TermSuggestOption> termSuggestOptions = searchSuggestionService.searchWithSuggestion(searchString);
        return termSuggestOptions.stream().map(TermSuggestOption::text).collect(Collectors.toList());
    }

    @GetMapping(value = "/termvectors")
    public List<String> getTermVectors() throws IOException {
        return searchSuggestionService.getAllTokens();
    }
}
