package com.olympic.dakar.result;

import com.olympic.dakar.result.dto.ResultRequest;
import com.olympic.dakar.result.dto.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@Tag(name = "Résultats", description = "Gestion des résultats et attribution automatique des médailles")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/api/v1/events/{eventId}/results")
    @Operation(summary = "Lister les résultats d'une épreuve, triés par position")
    public List<ResultResponse> findByEvent(@PathVariable Long eventId) {
        return resultService.findByEvent(eventId);
    }

    @GetMapping("/api/v1/events/{eventId}/podium")
    @Operation(summary = "Consulter le podium (médaillés) d'une épreuve")
    public List<ResultResponse> findPodium(@PathVariable Long eventId) {
        return resultService.findPodium(eventId);
    }

    @PostMapping("/api/v1/results")
    @Operation(summary = "Enregistrer un résultat (médaille attribuée automatiquement selon la position)")
    public ResponseEntity<ResultResponse> create(@Valid @RequestBody ResultRequest request) {
        ResultResponse created = resultService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/results/" + created.id())).body(created);
    }

    @GetMapping("/api/v1/results/{id}")
    @Operation(summary = "Consulter un résultat par id")
    public ResultResponse findById(@PathVariable Long id) {
        return resultService.findById(id);
    }

    @PutMapping("/api/v1/results/{id}")
    @Operation(summary = "Modifier un résultat (médaille recalculée automatiquement)")
    public ResultResponse update(@PathVariable Long id, @Valid @RequestBody ResultRequest request) {
        return resultService.update(id, request);
    }

    @DeleteMapping("/api/v1/results/{id}")
    @Operation(summary = "Supprimer un résultat")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
