package com.olympic.dakar.event;

import com.olympic.dakar.event.dto.EventPatchRequest;
import com.olympic.dakar.event.dto.EventRequest;
import com.olympic.dakar.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface EventService {

    Page<EventResponse> search(Long disciplineId, LocalDate date, Pageable pageable);

    EventResponse findById(Long id);

    EventResponse create(EventRequest request);

    EventResponse update(Long id, EventRequest request);

    EventResponse patch(Long id, EventPatchRequest request);

    void delete(Long id);
}
