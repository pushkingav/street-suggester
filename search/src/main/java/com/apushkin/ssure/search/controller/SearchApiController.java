package com.apushkin.ssure.search.controller;

import com.apushkin.ssure.search.service.SearchSuggestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController(value = "/search")
public class SearchApiController {

    private final SearchSuggestionService searchSuggestionService;

    public SearchApiController(SearchSuggestionService searchSuggestionService) {
        this.searchSuggestionService = searchSuggestionService;
    }

    @GetMapping
    public void search(@PathVariable String searchString) throws IOException {
        searchSuggestionService.searchWithSuggestion(searchString);
    }
}
