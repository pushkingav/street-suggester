package com.apushkin.ssure.search.controller;

import co.elastic.clients.elasticsearch.core.search.TermSuggestOption;
import com.apushkin.ssure.search.service.SearchSuggestionService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SearchApiController {

    private final SearchSuggestionService searchSuggestionService;

    public SearchApiController(SearchSuggestionService searchSuggestionService) {
        this.searchSuggestionService = searchSuggestionService;
    }

    @GetMapping(value = "/search")
    public List<String> search(@RequestParam String searchString) throws IOException {
        List<TermSuggestOption> termSuggestOptions = searchSuggestionService.searchWithSuggestion(searchString);
        return termSuggestOptions.stream().map(TermSuggestOption::text).collect(Collectors.toList());
    }
}
