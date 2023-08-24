package com.apushkin.cloud.function.csv;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class CloudFunctionCsvParser {
    /*
     * You need this main method (empty) or explicit
     * <start-class>com.apushkin.cloud.function.cs.CloudFunctionCsvParser</start-class>
     * in the POM to ensure boot plug-in makes the correct entry
     */
    public static void main(String[] args) {
        // empty unless using Custom runtime at which point it should include
        // SpringApplication.run(FunctionConfiguration.class, args);

    }

    @Bean
    public Function<String, String> parseCsv() {
        return value -> "CSV parsed from: " + value;
    }
}