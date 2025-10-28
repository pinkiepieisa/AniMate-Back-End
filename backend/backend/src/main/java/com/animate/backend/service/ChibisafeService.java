package com.animate.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;

@Service
public class ChibisafeService {

    private static final Logger logger = LoggerFactory.getLogger(ChibisafeService.class);

    @Value("${chibisafe.url:http://localhost:24424}")
    private String chibisafeUrl;

    @Value("${chibisafe.apikey:}")
    private String chibisafeApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean deleteFile(String uuid) {
        if (uuid == null || uuid.isBlank()) return false;
        String url = chibisafeUrl;
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        String deleteEndpoint = url + "/api/file/" + uuid;

        HttpHeaders headers = new HttpHeaders();
        if (chibisafeApiKey != null && !chibisafeApiKey.isBlank()) {
            headers.set("x-api-key", chibisafeApiKey);
        }
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(deleteEndpoint, HttpMethod.DELETE, entity, String.class);
            logger.debug("Deleted chibisafe file uuid={}", uuid);
            return true;
        } catch (RestClientException ex) {
            logger.warn("Failed to delete chibisafe file {}: {}", uuid, ex.getMessage());
            return false;
        }
    }

    @PostConstruct
    private void init() {
        logger.debug("ChibisafeService initialized with URL={}", chibisafeUrl);
    }
}
