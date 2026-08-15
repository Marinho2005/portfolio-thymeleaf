package com.vimar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vimar.dto.SettingsForm;
import com.vimar.dto.ProjectForm;
import com.vimar.dto.ContactForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private boolean configured() {
        return supabaseUrl != null && !supabaseUrl.isBlank()
                && supabaseKey != null && !supabaseKey.isBlank();
    }

    private HttpRequest.Builder baseRequest(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("apikey", supabaseKey)
                .header("Authorization", "Bearer " + supabaseKey)
                .timeout(Duration.ofSeconds(10));
    }

    private String endpoint(String resource) {
        String base = supabaseUrl.endsWith("/") ? supabaseUrl : supabaseUrl + "/";
        return base + "rest/v1/" + resource;
    }

    private HttpResponse<String> send(HttpRequest request) throws Exception {
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // ======================= SETTINGS =======================

    public Optional<SettingsForm> getSettings() {
        if (!configured()) {
            return Optional.empty();
        }
        try {
            String url = endpoint("settings?select=*&order=created_at.desc&limit=1");
            HttpResponse<String> resp = send(baseRequest(url).GET().build());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                logger.warn("getSettings failed: {} {}", resp.statusCode(), resp.body());
                return Optional.empty();
            }
            JsonNode arr = mapper.readTree(resp.body());
            if (!arr.isArray() || arr.isEmpty()) {
                return Optional.empty();
            }
            JsonNode row = arr.get(0);
            SettingsForm form = new SettingsForm();
            form.setDisplayName(row.path("display_name").asText(null));
            form.setEmail(row.path("email").asText(null));
            form.setTheme(row.path("theme").asText("light"));
            form.setPhotoUrl(row.path("photo_url").asText(null));
            form.setBio(row.path("bio").asText(null));
            form.setGithubUrl(row.path("github_url").asText(null));
            form.setLinkedinUrl(row.path("linkedin_url").asText(null));
            form.setInstagramUrl(row.path("instagram_url").asText(null));
            form.setTwitterUrl(row.path("twitter_url").asText(null));
            return Optional.of(form);
        } catch (Exception e) {
            logger.error("Failed to read settings", e);
            return Optional.empty();
        }
    }

    public boolean saveSettings(SettingsForm form) {
        if (!configured()) {
            logger.warn("Supabase URL or key not configured. Skipping save.");
            return false;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("display_name", form.getDisplayName());
            body.put("email", form.getEmail());
            body.put("theme", form.getTheme());
            body.put("photo_url", form.getPhotoUrl());
            body.put("bio", form.getBio());
            body.put("github_url", form.getGithubUrl());
            body.put("linkedin_url", form.getLinkedinUrl());
            body.put("instagram_url", form.getInstagramUrl());
            body.put("twitter_url", form.getTwitterUrl());
            String json = mapper.writeValueAsString(body);

            Optional<String> existingId = findSettingsId();
            if (existingId.isPresent()) {
                String url = endpoint("settings?id=eq." + URLEncoder.encode(existingId.get(), StandardCharsets.UTF_8));
                HttpRequest req = baseRequest(url)
                        .header("Prefer", "return=representation")
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> resp = send(req);
                logger.info("update settings: {} {}", resp.statusCode(), resp.body());
                return resp.statusCode() >= 200 && resp.statusCode() < 300;
            } else {
                String url = endpoint("settings");
                HttpRequest req = baseRequest(url)
                        .header("Prefer", "return=representation")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> resp = send(req);
                logger.info("insert settings: {} {}", resp.statusCode(), resp.body());
                return resp.statusCode() >= 200 && resp.statusCode() < 300;
            }
        } catch (Exception e) {
            logger.error("Failed to save settings", e);
            return false;
        }
    }

    private Optional<String> findSettingsId() {
        try {
            String url = endpoint("settings?select=id&order=created_at.desc&limit=1");
            HttpResponse<String> resp = send(baseRequest(url).GET().build());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                return Optional.empty();
            }
            JsonNode arr = mapper.readTree(resp.body());
            if (!arr.isArray() || arr.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(arr.get(0).path("id").asText());
        } catch (Exception e) {
            logger.error("Failed to find settings id", e);
            return Optional.empty();
        }
    }

    // ======================= CONTACT =======================

    public boolean sendContactMessage(ContactForm form) {
        if (!configured()) {
            logger.warn("Supabase URL or key not configured. Skipping contact message.");
            return false;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", form.getName());
            body.put("email", form.getEmail());
            body.put("message", form.getMessage());
            String json = mapper.writeValueAsString(body);
            HttpRequest req = baseRequest(endpoint("contact_messages"))
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> resp = send(req);
            logger.info("contact message: {} {}", resp.statusCode(), resp.body());
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            logger.error("Failed to send contact message", e);
            return false;
        }
    }

    // ======================= PROJECTS =======================

    public List<Map<String, Object>> getProjects() {
        if (!configured()) {
            return List.of();
        }
        try {
            String url = endpoint("projects?select=*&order=created_at.desc");
            HttpResponse<String> resp = send(baseRequest(url).GET().build());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                logger.warn("getProjects failed: {} {}", resp.statusCode(), resp.body());
                return List.of();
            }
            JsonNode arr = mapper.readTree(resp.body());
            List<Map<String, Object>> projects = new ArrayList<>();
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    projects.add(mapper.convertValue(node, Map.class));
                }
            }
            return projects;
        } catch (Exception e) {
            logger.error("Failed to read projects", e);
            return List.of();
        }
    }

    public boolean createProject(ProjectForm form) {
        if (!configured()) {
            return false;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("title", form.getTitle());
            body.put("description", form.getDescription());
            body.put("link", form.getLink());
            String json = mapper.writeValueAsString(body);
            HttpRequest req = baseRequest(endpoint("projects"))
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> resp = send(req);
            logger.info("create project: {} {}", resp.statusCode(), resp.body());
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            logger.error("Failed to create project", e);
            return false;
        }
    }

    public boolean updateProject(String id, ProjectForm form) {
        if (!configured()) {
            return false;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("title", form.getTitle());
            body.put("description", form.getDescription());
            body.put("link", form.getLink());
            String json = mapper.writeValueAsString(body);
            String url = endpoint("projects?id=eq." + URLEncoder.encode(id, StandardCharsets.UTF_8));
            HttpRequest req = baseRequest(url)
                    .header("Prefer", "return=representation")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> resp = send(req);
            logger.info("update project: {} {}", resp.statusCode(), resp.body());
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            logger.error("Failed to update project", e);
            return false;
        }
    }

    public boolean deleteProject(String id) {
        if (!configured()) {
            return false;
        }
        try {
            String url = endpoint("projects?id=eq." + URLEncoder.encode(id, StandardCharsets.UTF_8));
            HttpRequest req = baseRequest(url).DELETE().build();
            HttpResponse<String> resp = send(req);
            logger.info("delete project: {}", resp.statusCode());
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            logger.error("Failed to delete project", e);
            return false;
        }
    }
}