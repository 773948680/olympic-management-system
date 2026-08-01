package com.olympic.dakar.result.dto;

import com.olympic.dakar.result.MedalType;

public record ResultResponse(
        Long id,
        Long eventId,
        String eventName,
        Long athleteId,
        String athleteFirstName,
        String athleteLastName,
        Integer position,
        String time,
        Double score,
        MedalType medal
) {
}
