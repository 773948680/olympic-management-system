package com.olympic.dakar.event;

import com.olympic.dakar.common.dto.PageResponse;
import com.olympic.dakar.event.dto.EventPatchRequest;
import com.olympic.dakar.event.dto.EventRequest;
import com.olympic.dakar.event.dto.EventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Épreuves", description = "Gestion des épreuves")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @Operation(summary = "Lister/rechercher les épreuves par discipline et/ou date (paginé, filtres combinables)")
    public PageResponse<EventResponse> search(
            @RequestParam(required = false) Long disciplineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(sort = "id") Pageable pageable) {
        return PageResponse.from(eventService.search(disciplineId, date, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une épreuve par id")
    public EventResponse findById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer une épreuve")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        EventResponse created = eventService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/events/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une épreuve")
    public EventResponse update(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return eventService.update(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modifier partiellement une épreuve")
    public EventResponse patch(@PathVariable Long id, @Valid @RequestBody EventPatchRequest request) {
        return eventService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une épreuve")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
