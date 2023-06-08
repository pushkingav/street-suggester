package com.apushkin.ssure.search.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.TermSuggestOption;
import com.apushkin.ssure.search.model.ElasticStoreAddress;
import com.apushkin.ssure.search.service.SearchSuggestionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
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
                               @RequestParam(required = false) String state,
                               @RequestParam boolean strict) throws IOException {
        log.info("Searching for: pharmaName: {}, address: {}, zip: {}, city: {}, state: {}, strict: {}", pharmaName, address, zip,
                city, state, strict);
        List<String> terms = StringUtils.isBlank(pharmaName)
                ? Collections.emptyList()
                : searchSuggestionService.splitBySpace(pharmaName);
        if (!terms.isEmpty()) {
            List<String> candidates = new ArrayList<>();
            for (String term : terms) {
                if (!term.contains("-") && term.length() > 10) {
                    log.info("Using prefix tree to compute candidates");
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
                log.info("Found candidates: {}", candidates);
                pharmaName = String.join(" ", candidates);
                log.info("Computed pharmacy name is {}", pharmaName);
            }
        }
        List<String> result = new ArrayList<>();
        SearchResponse<ElasticStoreAddress> response = getResponse(pharmaName, address, zip, city, state, strict);
        //try to search for compound pharmaName
        SearchResponse<ElasticStoreAddress> response2 = null;
        if (terms.size() > 1) {
            response2 = getResponse(String.join("", terms), address, zip, city, state, strict);
        }
        if (response2 != null) {
            result.addAll(searchSuggestionService.convertResponse(Arrays.asList(response, response2)));
        } else {
            result.addAll(searchSuggestionService.convertResponse(Collections.singletonList(response)));
        }
        return result;
    }

    private SearchResponse<ElasticStoreAddress> getResponse(String pharmaName, String address, String zip, String city,
                                                            String state, boolean strict) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(pharmaName, address, zip, city, state, true, strict);
        if (response.hits().hits().size() == 0 || !strict) {
            response = searchSuggestionService
                    .searchMultipleFields(pharmaName, address, zip, city, state, false, strict);
        }
        return response;
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
