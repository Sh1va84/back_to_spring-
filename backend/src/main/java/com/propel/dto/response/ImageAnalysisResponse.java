package com.propel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisResponse {
    private boolean success;
    private String description;
    private String error;

    public static ImageAnalysisResponse success(String description) {
        return ImageAnalysisResponse.builder()
                .success(true)
                .description(description)
                .build();
    }

    public static ImageAnalysisResponse error(String error) {
        return ImageAnalysisResponse.builder()
                .success(false)
                .error(error)
                .build();
    }
}
