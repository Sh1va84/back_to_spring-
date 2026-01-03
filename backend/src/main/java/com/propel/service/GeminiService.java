package com.propel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propel.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final GeminiConfig geminiConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private static final String MAINTENANCE_ANALYSIS_PROMPT =
        "You are a property maintenance expert. Analyze this image and generate a professional maintenance request description.\n" +
        "Include:\n\n" +
        "- Issue identified in the image\n" +
        "- Severity level (Minor/Moderate/Severe/Critical)\n" +
        "- Recommended repair action\n" +
        "- Safety concerns (if any)\n" +
        "- Suggested trade/professional needed\n\n" +
        "Write as clear, flowing paragraphs (150-200 words). Be specific and professional. " +
        "If image is unclear or not maintenance-related, say: \"Unable to identify maintenance issue. " +
        "Please upload a clearer image of the problem.\"";

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public String analyzeMaintenanceImage(MultipartFile image) {
        try {
            // Validate file
            validateImage(image);

            // Convert image to base64
            String base64Image = convertToBase64(image);
            String mimeType = image.getContentType();

            // Build request payload
            Map<String, Object> requestBody = buildGeminiRequest(base64Image, mimeType);

            // Call Gemini API
            WebClient webClient = webClientBuilder.build();

            String response = webClient.post()
                    .uri(geminiConfig.getFullApiUrl())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error("Gemini API error response: {}", errorBody);
                                return Mono.error(new RuntimeException("API Error: " + errorBody));
                            })
                    )
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        log.error("Error calling Gemini API: {}", e.getMessage());
                        return Mono.just("{\"error\": \"" + e.getMessage() + "\"}");
                    })
                    .block();

            // Parse response
            return parseGeminiResponse(response);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error analyzing image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze image: " + e.getMessage());
        }
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("No image file provided");
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, and WEBP images are allowed");
        }

        if (image.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
    }

    private String convertToBase64(MultipartFile image) throws Exception {
        byte[] bytes = image.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    private Map<String, Object> buildGeminiRequest(String base64Image, String mimeType) {
        Map<String, Object> requestBody = new HashMap<>();

        // Build contents array
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(
            Map.of("text", MAINTENANCE_ANALYSIS_PROMPT),
            Map.of(
                "inline_data", Map.of(
                    "mime_type", mimeType,
                    "data", base64Image
                )
            )
        ));

        requestBody.put("contents", List.of(content));

        // Add generation config for better output
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.4);
        generationConfig.put("topK", 32);
        generationConfig.put("topP", 1);
        generationConfig.put("maxOutputTokens", 1024);
        requestBody.put("generationConfig", generationConfig);

        // Add safety settings
        List<Map<String, Object>> safetySettings = List.of(
            Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
            Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
            Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
            Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE")
        );
        requestBody.put("safetySettings", safetySettings);

        return requestBody;
    }

    private String parseGeminiResponse(String response) throws Exception {
        JsonNode rootNode = objectMapper.readTree(response);

        // Check for API errors
        if (rootNode.has("error")) {
            String errorMessage = rootNode.get("error").asText();
            log.error("Gemini API error: {}", errorMessage);
            throw new RuntimeException("AI service error: " + errorMessage);
        }

        // Extract text from response
        JsonNode candidates = rootNode.get("candidates");
        if (candidates != null && candidates.isArray() && candidates.size() > 0) {
            JsonNode firstCandidate = candidates.get(0);
            JsonNode content = firstCandidate.get("content");
            if (content != null) {
                JsonNode parts = content.get("parts");
                if (parts != null && parts.isArray() && parts.size() > 0) {
                    JsonNode text = parts.get(0).get("text");
                    if (text != null) {
                        return text.asText().trim();
                    }
                }
            }
        }

        throw new RuntimeException("Unable to parse AI response. Please try again.");
    }
}
