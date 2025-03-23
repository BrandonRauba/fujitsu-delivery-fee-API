package com.example.feecalculator.documentation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response when something goes wrong")
public class ErrorResponse {
    @Schema(description = "Error message", example = "Invalid city or vehicle type!")
    private String message;

    public ErrorResponse(boolean correct, String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}