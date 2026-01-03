package com.propel.controller;

import com.propel.dto.response.ImageAnalysisResponse;
import com.propel.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/maintenance")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ImageAnalysisController {

    private final GeminiService geminiService;

    @PostMapping("/analyze-image")
    public ResponseEntity<ImageAnalysisResponse> analyzeImage(
            @RequestParam("image") MultipartFile image) {

        log.info("Received image analysis request: filename={}, size={}, type={}",
                image.getOriginalFilename(),
                image.getSize(),
                image.getContentType());

        try {
            String description = geminiService.analyzeMaintenanceImage(image);

            log.info("Successfully analyzed image: {}", image.getOriginalFilename());

            return ResponseEntity.ok(ImageAnalysisResponse.success(description));

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ImageAnalysisResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("Error analyzing image: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ImageAnalysisResponse.error(
                        "Failed to analyze image. Please try again later."));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Image Analysis Service is running");
    }
}
