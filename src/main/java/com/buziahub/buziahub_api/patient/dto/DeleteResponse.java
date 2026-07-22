package com.buziahub.buziahub_api.patient.dto;

public record DeleteResponse(
        String message,
        long deletedCount,
        long timeStamp
) {
}
