package com.olympic.dakar.athlete;

import com.olympic.dakar.athlete.dto.AthletePatchRequest;
import com.olympic.dakar.athlete.dto.AthleteRequest;
import com.olympic.dakar.athlete.dto.AthleteResponse;
import com.olympic.dakar.common.dto.PageResponse;
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
@RequestMapping("/api/v1/athletes")
@Tag(name = "Athlètes", description = "Gestion des athlètes")
public class AthleteController {

    private final AthleteService athleteService;

    public AthleteController(AthleteService athleteService) {
        this.athleteService = athleteService;
    }

    @GetMapping
    @Operation(summary = "Lister/rechercher les athlètes (paginé, multicritère)")
    public PageResponse<AthleteResponse> search(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Long disciplineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bornAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bornBefore,
            @PageableDefault(sort = "id") Pageable pageable) {
        return PageResponse.from(athleteService.search(lastName, firstName, gender, nationality,
                disciplineId, bornAfter, bornBefore, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un athlète par id")
    public AthleteResponse findById(@PathVariable Long id) {
        return athleteService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer un athlète")
    public ResponseEntity<AthleteResponse> create(@Valid @RequestBody AthleteRequest request) {
        AthleteResponse created = athleteService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/athletes/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Remplacer intégralement un athlète")
    public AthleteResponse update(@PathVariable Long id, @Valid @RequestBody AthleteRequest request) {
        return athleteService.update(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modifier partiellement un athlète")
    public AthleteResponse patch(@PathVariable Long id, @Valid @RequestBody AthletePatchRequest request) {
        return athleteService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un athlète")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        athleteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
