package com.olympic.dakar.event.dto;

import com.olympic.dakar.event.EventStatus;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String name,
        Long disciplineId,
        String disciplineName,
        LocalDateTime eventDate,
        String venue,
        EventStatus status
) {
}
