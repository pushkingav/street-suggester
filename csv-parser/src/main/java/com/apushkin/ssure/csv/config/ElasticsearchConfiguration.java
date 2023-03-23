package com.apushkin.ssure.csv.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.apushkin.ssure.csv.repository"})
@ComponentScan(basePackages = {"com.apushkin.ssure.csv.model"})
public class ElasticsearchConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfiguration.class);

    @Bean
    public ElasticsearchClient restClient(@Value("${elastic.host}") String host,
                                          @Value("${elastic.port}") int port) {
        RestClient restClient = RestClient.builder(new HttpHost(host, port)).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        logger.info("Elasticsearch host: {}", host);
        return new ElasticsearchClient(transport);
    }
}
