package com.apushkin.ssure.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserInputAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(UserInputAnalyzer.class);
    public static final String REG_NUMBER = "[0-9]";
    public static final String REG_CHAR = "[a-z]";

    /**
     * Used to analyze user input in address field and convert it to a
     * regexp String which then will be sent to Elasticsearch
     */
    public List<String> convertAddressToRegexp(String address, boolean strict) {
        List<String> result = new ArrayList<>();
        String[] parts = address.split(" ");
        for (String part : parts) {
            StringBuilder regexpStr = new StringBuilder();
            char[] chars = part.toCharArray();
            if (chars.length > 1) {
                regexpStr.append(chars[0]).append(".*");
            } else {
                regexpStr.append(chars[0]);
            }
            result.add(regexpStr.toString());
            logger.info("Regexp for address: {}", regexpStr);
        }
        return result;
    }
}
