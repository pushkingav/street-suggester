package com.apushkin.ssure.search.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApplicationContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    private final SearchSuggestionService searchSuggestionService;

    public ApplicationContextRefreshedEventListener(SearchSuggestionService searchSuggestionService) {
        this.searchSuggestionService = searchSuggestionService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            searchSuggestionService.searchInsideElastic("WALTON");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
