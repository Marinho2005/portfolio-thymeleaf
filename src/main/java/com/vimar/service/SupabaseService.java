package com.vimar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vimar.dto.SettingsForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseService {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseService.class);

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public boolean saveSettings(SettingsForm form) {
        if (supabaseUrl == null || supabaseUrl.isBlank() || supabaseKey == null || supabaseKey.isBlank()) {
            logger.warn("Supabase URL or key not configured. Skipping save.");
            return false;
        }

        try {
            String endpoint = supabaseUrl.endsWith("/") ? supabaseUrl + "rest/v1/settings" : supabaseUrl + "/rest/v1/settings";

            Map<String, Object> body = new HashMap<>();
            body.put("display_name", form.getDisplayName());
            body.put("email", form.getEmail());
            body.put("theme", form.getTheme());

            String json = mapper.writeValueAsString(body);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            logger.info("Supabase response: {} {}", resp.statusCode(), resp.body());
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            logger.error("Failed to save settings to Supabase", e);
            return false;
        }
    }
}
