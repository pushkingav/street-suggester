package com.apushkin.ssure.search.config;

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
@EnableElasticsearchRepositories(basePackages = {"com.apushkin.ssure.search.repository"})
@ComponentScan(basePackages = {"com.apushkin.ssure.search.model"})
public class ElasticsearchConfiguration {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public ElasticsearchClient restClient(@Value("${elastic.host}") String host,
                                          @Value("${elastic.port}") int port) {
        logger.info("Elasticsearch host = {}", host);
        RestClient restClient = RestClient.builder(new HttpHost(host, port)).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
