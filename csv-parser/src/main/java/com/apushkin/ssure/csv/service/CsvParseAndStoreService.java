package com.apushkin.ssure.csv.service;

import com.apushkin.ssure.csv.model.StreetName;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    private List<String> parsedNames;

    public void parseCsv() throws IOException {
        LOGGER.info(String.format("Found csv file: %s", filename));
        List<StreetName> streetNames = new CsvToBeanBuilder(new FileReader(filename))
                .withType(StreetName.class).build().parse();
        LOGGER.info(String.format("Parsed %d lines", streetNames.size()));

        this.parsedNames = streetNames.stream().map(StreetName::getName).collect(Collectors.toList());
        streetNames.clear();

        if (needStorage) {
            storeToElastic();
        }
    }

    private void storeToElastic() {
        //Store street names in ElasticSearch
        LOGGER.info("Storing {} street names to ElasticSearch", parsedNames.size());
    }
}
