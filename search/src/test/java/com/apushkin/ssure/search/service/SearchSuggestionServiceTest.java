package com.apushkin.ssure.search.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.apushkin.ssure.search.model.ElasticStoreAddress;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *  This test class must use the Elasticsearch up and running.
 *  Also, you need to index the appropriate csv into Elasticsearch.
 */
@SpringBootTest
@ActiveProfiles("test")
class SearchSuggestionServiceTest {

    @Autowired
    SearchSuggestionService searchSuggestionService;

    /**
     * If there is a space separating # and store name it may cause problems but not in our search.
     * Search for "Costco Pharmacy # 462" should return the exact match
     */
    @ParameterizedTest
    @ValueSource(strings = {"Costco Pharmacy # 462", "Costco Pharmacy #462", "Costco Pharmacy 462"})
    void searchForPharmacyContainingHashSign(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "costco pharmacy # 462";
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Here the input string is not CVS pharmacy like most
     * Search for "CVS 17021 In Target" should return the exact match
     */
    @ParameterizedTest
    @ValueSource(strings = {"CVS 17021 In Target"})
    void searchForPharmacyThatIsNotPharmacyNameLikeMost(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "cvs 17021 in target";
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Name is CVS/Pharmacy #2340 - the slash in the name causes issues
     */
    @ParameterizedTest
    @ValueSource(strings = {"CVS/Pharmacy #2340", "CVS Pharmacy #2340"})
    void searchForPharmacyContainingSlash(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "cvs/pharmacy #2340";
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * The apostrophe in the expected name. If someone searched Ralphs or Ralph vs Ralph's
     */
    @ParameterizedTest
    @ValueSource(strings = {"Ralph's Pharmacy INC", "Ralphs Pharmacy INC", "Ralph Pharmacy INC"})
    void searchForPharmacyContainingApostrophe(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "ralph's pharmacy inc.";
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * The apostrophe in the search. Issue is walgreens vs searching walgreen's.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Walgreens Drugstore #19882", "Walgreen's Drugstore #19882", "Walgreen Drugstore #19882"})
    void searchWithUnexpectedApostrophe(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "walgreens drugstore #19882";
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * People will type Expressrx instead of express Rx
     */
    @ParameterizedTest
    @ValueSource(strings = {"Express Rx of Paris", "Expressrx of Paris"})
    void searchTogether(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "express rx of paris";
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Some people search express-scripts or expressscripts
     */
    @ParameterizedTest
    @ValueSource(strings = {"EXPRESS SCRIPTS HOME DELIVERY", "EXPRESS-SCRIPTS HOME DELIVERY", "EXPRESSSCRIPTS HOME DELIVERY"})
    void searchTogetherOrWithDash(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "express scripts home delivery";
        //FIXME - for the 3rd case the expected result is on the 7th position instead of 1st. Needs tuning.
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * "Claremore compounding center". People type the name Clare More or Claremore pharmacy
     */
    @ParameterizedTest
    @ValueSource(strings = {"Claremore compounding center", "Claremore pharmacy", "Clare More pharmacy"})
    void searchWithUnexpectedTerm(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "claremore compounding center";
        //FIXME - for the 2nd case the expected result is on the 2nd position instead of 1st.
        //FIXME - for the 3rd case the expected result is out of the first 10 or even not found.
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * "PPC - ProCare Pharmacy Care (miramar)". People type procare or Pro Care.
     */
    @ParameterizedTest
    @ValueSource(strings = {"PPC - ProCare Pharmacy", "PPC - procare Pharmacy", "PPC - Pro Care Pharmacy"})
    void searchWithSpace(String input) throws IOException {
        SearchResponse<ElasticStoreAddress> response = searchSuggestionService
                .searchMultipleFields(input, "", "", "", "");
        assertNotNull(response);
        List<Hit<ElasticStoreAddress>> hits = response.hits().hits();
        assertNotNull(hits.get(0).source());
        String expected = "ppc - procare pharmacy care (mirama";
        assertEquals(expected, hits.get(0).source().getBusinessName().toLowerCase(Locale.ENGLISH));
    }
}