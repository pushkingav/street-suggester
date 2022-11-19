package com.apushkin.ssure.search.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.apushkin.ssure.search.model.ElasticStoreAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class SearchSuggestionServiceTest {

    @Autowired
    SearchSuggestionService searchSuggestionService;

    /**
     * Search for "Costco Pharmacy # 462" should return the exact match
     * */
    @Test
    void searchForPharmacyContainingHashSign() throws IOException {
        //Search like real pharmacy name is typed in index
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields("Costco Pharmacy # 462", "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        assertEquals("costco pharmacy # 462", hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));

        //Search without space before the hash sign
        response = searchSuggestionService.searchMultipleFields("Costco Pharmacy #462", "", "", "", "");
        assertNotNull(response);
        hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        assertEquals("costco pharmacy # 462", hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));

        //Search without hash sign
        response = searchSuggestionService.searchMultipleFields("Costco Pharmacy 462", "", "", "", "");
        assertNotNull(response);
        hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        assertEquals("costco pharmacy # 462", hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }
}