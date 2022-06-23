package com.apushkin.ssure.csv;

import com.apushkin.ssure.csv.service.CsvParseAndStoreService;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class CsvParserApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvParserApplication.class);

    private final CsvParseAndStoreService csvParseAndStoreService;

    public CsvParserApplication(CsvParseAndStoreService csvParseAndStoreService) {
        this.csvParseAndStoreService = csvParseAndStoreService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CsvParserApplication.class, args);
    }

    @Override
    public void run(String... args) throws CsvValidationException, IOException {
        LOGGER.info("Startup of CSV parser");
        csvParseAndStoreService.parseCsv();
    }
}