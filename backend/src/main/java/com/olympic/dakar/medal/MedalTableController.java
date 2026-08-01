package com.olympic.dakar.medal;

import com.olympic.dakar.medal.dto.MedalTableEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medals")
@Tag(name = "Médailles", description = "Tableau des médailles par nation")
public class MedalTableController {

    private final MedalTableService medalTableService;

    public MedalTableController(MedalTableService medalTableService) {
        this.medalTableService = medalTableService;
    }

    @GetMapping("/medal-table")
    @Operation(summary = "Tableau des médailles trié par Or puis Argent puis Bronze")
    public List<MedalTableEntry> getMedalTable() {
        return medalTableService.getMedalTable();
    }
}
