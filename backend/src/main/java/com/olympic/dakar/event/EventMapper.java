package com.olympic.dakar.event;

import com.olympic.dakar.event.dto.EventResponse;

public final class EventMapper {

    private EventMapper() {
    }

    public static EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDiscipline().getId(),
                event.getDiscipline().getName(),
                event.getEventDate(),
                event.getVenue(),
                event.getStatus()
        );
    }
}
