package com.ergoproxy.backend.service;

import com.ergoproxy.backend.dto.response.QuoteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class QuoteService {

    private static final Logger log = LoggerFactory.getLogger(QuoteService.class);

    public QuoteResponse getRandomQuote() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(
                "https://meowfacts.herokuapp.com/?lang=esp",
                Map.class
            );
            
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                List<String> data = (List<String>) response.get("data");
                if (data != null && !data.isEmpty()) {
                    return new QuoteResponse(data.get(0));
                }
            }
        } catch (Exception e) {
            log.error("Error al obtener frase de la API externa", e);
        }
        
        return new QuoteResponse("La creatividad es la inteligencia divirtiéndose.");
    }
}