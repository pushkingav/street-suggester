package com.apushkin.ssure.csv;

import com.apushkin.ssure.csv.service.CsvParseAndStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;

import java.io.IOException;

@SpringBootApplication
public class CsvParserApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvParserApplication.class);

    private final CsvParseAndStoreService csvParseAndStoreService;
    private ConfigurableApplicationContext context;

    public CsvParserApplication(CsvParseAndStoreService csvParseAndStoreService, ConfigurableApplicationContext ctx) {
        this.csvParseAndStoreService = csvParseAndStoreService;
        this.context = ctx;
    }

    public static void main(String[] args) {
        SpringApplication.run(CsvParserApplication.class, args);
    }

    @Override
    public void run(String... args) {
        LOGGER.info("Startup of CSV parser");
        try {
            csvParseAndStoreService.parseCsv();
        } catch (IOException e) {
            LOGGER.error("Cannot parse csv file, exiting.", e);
            System.exit(SpringApplication.exit(context));
        }
        try {
            csvParseAndStoreService.storeToElastic();
        } catch (DataAccessResourceFailureException e) {
            LOGGER.warn("Something went wrong while storing to Elastic, but it doesn't mean that nothing was stored",
                            e);
        } finally {
            System.exit(SpringApplication.exit(context));
        }
    }
}