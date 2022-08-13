package com.apushkin.ssure.search.controller;

import com.apushkin.ssure.search.service.SearchSuggestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SearchApiController {

    private final SearchSuggestionService searchSuggestionService;

    public SearchApiController(SearchSuggestionService searchSuggestionService) {
        this.searchSuggestionService = searchSuggestionService;
    }

    @GetMapping(value = "/search")
    public void search(@RequestParam String searchString) throws IOException {
        searchSuggestionService.searchWithSuggestion(searchString);
    }
}
