package com.apushkin.ssure.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import com.apushkin.ssure.search.model.ElasticStoreAddress;
import com.apushkin.ssure.search.model.ElasticStreetName;
import com.apushkin.ssure.search.model.SearchField;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Search for terms and suggestions in Elasticsearch instance.
 * This version of the service is compatible and was designed for Elasticsearch 8.2.3
 */

@Service
public class SearchSuggestionService {
    private static final Logger logger = LoggerFactory.getLogger(SearchSuggestionService.class);
    private final ElasticsearchClient client;
    private Trie<String, String> trie;

    public SearchSuggestionService(ElasticsearchClient client) {
        this.client = client;
    }

    @PostConstruct
    public void init() {
        this.trie = buidTrie();
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

    public List<String> findTerms(String inputQuery) {
        String query = inputQuery.trim().toLowerCase(Locale.US);
        String[] parts = query.split(" ");
        return Arrays.asList(parts);
    }

    public SearchResponse<ElasticStoreAddress> searchMultipleFields(String pharmaName, String address, String zip,
                                                                    String city, String state, boolean isPhrase)
            throws IOException {
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
        List<String> allIds = new ArrayList<>();
        String scrollId;
        SearchResponse<ElasticStoreAddress> response = client.search(s -> s
                        .scroll(Time.of(builder -> builder.time("30s")))
                        .index("addresses")
                        .query(q -> q
                                .matchAll(v -> QueryBuilders.matchAll())
                        )
                        .size(1000),
                ElasticStoreAddress.class
        );
        List<String> ids = response.hits().hits().stream().map(Hit::id).toList();
        allIds.addAll(ids);
        scrollId = response.scrollId();
        ScrollResponse<ElasticStoreAddress> sc;
        do {
            String finalScrollId = scrollId;
            sc = client
                    .scroll(ScrollRequest.of(builder -> builder
                                    .scrollId(finalScrollId)
                                    .scroll(Time.of(t -> t.time("1m")))),
                            ElasticStoreAddress.class);
            ids = sc.hits().hits().stream().map(Hit::id).toList();
            allIds.addAll(ids);
            scrollId = sc.scrollId();
        } while (sc.hits().hits().size() != 0);
        String finalScrollId1 = scrollId;
        client.clearScroll(ClearScrollRequest.of(b -> b.scrollId(finalScrollId1)));
        logger.info("Size of allIds: {}", allIds.size());

        ConcurrentLinkedQueue<String> terms = new ConcurrentLinkedQueue<>();

        allIds.parallelStream().forEach(id -> {
            TermvectorsResponse termVectors;
            try {
                termVectors = client.termvectors(builder -> builder
                        .index("addresses")
                        .fields(Collections.singletonList(SearchField.BUSINESS_NAME.toString()))
                        .id(id));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            termVectors.termVectors().get("businessName").terms().keySet().forEach(t -> terms.add(t));
        });
        Set<String> termSet = new TreeSet<>(terms);
        List<String> sorted = termSet.stream().filter(term -> term.length() > 1).sorted().toList();
        logger.info("Terms size: {}", terms.size());

        return new ArrayList<>(terms);
    }

    public Trie<String, String> buidTrie() {
        Trie<String, String> trie = new PatriciaTrie<>();
        InputStream txtFileResource;
        try {
            txtFileResource = new ClassPathResource("terms.txt").getInputStream();
        } catch (IOException e) {
            logger.error("Cannot read terms file.", e);
            throw new RuntimeException(e);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(txtFileResource))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.substring(0, line.length() - 1); //get rid of finishing comma
                if (StringUtils.isNumeric(line)) {
                    continue; //we do not take into account terms that consist of numbers only
                }
                trie.put(line, line); //yes, key and value are equal
            }
        } catch (IOException e) {
            logger.error("Error while parsing terms file.", e);
            throw new RuntimeException(e);
        }
        logger.info("Built prefix tree of size {}", trie.size());
        return trie;
    }

    public List<String> findCandidates(String searchStr) {
        int lastIndex = 0; //index of the last char in searchStr that produces terms
        List<String> result = new ArrayList<>();
        for (int i = 0; i < searchStr.length(); i++) {
            lastIndex = i;
            String subQuery = searchStr.substring(0, i);
            SortedMap<String, String> prefixMap = trie.prefixMap(subQuery);
            if (prefixMap.isEmpty()) {
                //finish, candidates not found
                result.add(searchStr.substring(0, lastIndex - 1));
                break;
            }
            if (i == searchStr.length() - 1) { //if we reach the end of searchStr -> this is a candidate also...
                result.add(searchStr);
            }
        }
        if (lastIndex + 1 < searchStr.length()) {
            List<String> restCandidates = findCandidates(searchStr.substring(--lastIndex));
            result.addAll(restCandidates);
            logger.debug("Candidates: {}", restCandidates);
        }
        logger.info("Found {} candidates", result.size());
        return result;
    }

    private void printPrefixMap(SortedMap<String, String> prefixMap) {
        System.out.println("-------");
        for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
            System.out.println("Key: <" + entry.getKey() + ">, Value: <" + entry.getValue() + ">");
        }
        System.out.println("-------");
    }
}
