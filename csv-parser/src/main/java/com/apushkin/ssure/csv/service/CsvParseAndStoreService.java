package com.apushkin.ssure.csv.service;

import com.apushkin.ssure.csv.model.ElasticStoreAddress;
import com.apushkin.ssure.csv.model.StoreAddress;
import com.apushkin.ssure.csv.repository.ElasticsearchIngestRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The service is intended to read csv file containing street names and store them into the ElasticSearch
 * */
@Service
public class CsvParseAndStoreService {
    public static final Logger LOGGER = LoggerFactory.getLogger(CsvParseAndStoreService.class);

    @Value("${csv.filename}")
    private String filename;

    @Value("${store.to.elastic}")
    private boolean needStorage;

    private final List<StoreAddress> parsedAddresses = new ArrayList<>();

    private final ElasticsearchIngestRepository repository;

    public CsvParseAndStoreService(ElasticsearchIngestRepository repository) {
        this.repository = repository;
    }

    public void parseCsv() throws IOException {
        LOGGER.info(String.format("Found csv file: %s", filename));
        List<StoreAddress> addressList = new CsvToBeanBuilder(new FileReader(filename))
                .withType(StoreAddress.class).build().parse();
        LOGGER.info(String.format("Parsed %d lines", addressList.size()));

        parsedAddresses.addAll(addressList);
        addressList.clear();
    }

    public void storeToElastic() {
        if (!needStorage) {
            LOGGER.info("Will not store data to Elasticsearch due to store.to.elastic parameter");
            return;
        }
        LOGGER.info("Saving {} store addresses to ElasticSearch", parsedAddresses.size());
        List<ElasticStoreAddress> elasticStoreAddresses = parsedAddresses
                .stream()
                .map(parsedAddress -> new ElasticStoreAddress(parsedAddress.getCity(), parsedAddress.getAddressLine()))
                .collect(Collectors.toList());

        repository.saveAll(elasticStoreAddresses);

        Iterable<ElasticStoreAddress> all = repository.findAll();
        long count = StreamSupport.stream(all.spliterator(), false).count();
        LOGGER.info("Stored {} store addresses to ElasticSearch", count);
    }
}
