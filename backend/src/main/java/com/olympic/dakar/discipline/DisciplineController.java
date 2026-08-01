package com.olympic.dakar.discipline;

import com.olympic.dakar.athlete.AthleteService;
import com.olympic.dakar.athlete.dto.AthleteResponse;
import com.olympic.dakar.common.dto.PageResponse;
import com.olympic.dakar.discipline.dto.DisciplinePatchRequest;
import com.olympic.dakar.discipline.dto.DisciplineRequest;
import com.olympic.dakar.discipline.dto.DisciplineResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/disciplines")
@Tag(name = "Disciplines", description = "Gestion des disciplines sportives")
public class DisciplineController {

    private final DisciplineService disciplineService;
    private final AthleteService athleteService;

    public DisciplineController(DisciplineService disciplineService, AthleteService athleteService) {
        this.disciplineService = disciplineService;
        this.athleteService = athleteService;
    }

    @GetMapping
    @Operation(summary = "Lister les disciplines (paginé)")
    public PageResponse<DisciplineResponse> findAll(@PageableDefault(sort = "id") Pageable pageable) {
        return PageResponse.from(disciplineService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une discipline par id")
    public DisciplineResponse findById(@PathVariable Long id) {
        return disciplineService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer une discipline")
    public ResponseEntity<DisciplineResponse> create(@Valid @RequestBody DisciplineRequest request) {
        DisciplineResponse created = disciplineService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/disciplines/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une discipline")
    public DisciplineResponse update(@PathVariable Long id, @Valid @RequestBody DisciplineRequest request) {
        return disciplineService.update(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modifier partiellement une discipline")
    public DisciplineResponse patch(@PathVariable Long id, @Valid @RequestBody DisciplinePatchRequest request) {
        return disciplineService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une discipline")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        disciplineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/athletes")
    @Operation(summary = "Lister les athlètes d'une discipline (paginé)")
    public PageResponse<AthleteResponse> findAthletes(@PathVariable Long id, @PageableDefault(sort = "id") Pageable pageable) {
        return PageResponse.from(athleteService.findByDiscipline(id, pageable));
    }
}
