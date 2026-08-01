package com.olympic.dakar.dashboard;

import com.olympic.dakar.dashboard.dto.CountResponse;
import com.olympic.dakar.dashboard.dto.CountryMedalistsEntry;
import com.olympic.dakar.dashboard.dto.CountryRankingEntry;
import com.olympic.dakar.dashboard.dto.MedalTotalsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Statistiques globales des Jeux Olympiques")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/athletes/count")
    @Operation(summary = "Nombre total d'athlètes")
    public CountResponse getAthletesCount() {
        return dashboardService.countAthletes();
    }

    @GetMapping("/countries/count")
    @Operation(summary = "Nombre de pays participants")
    public CountResponse getCountriesCount() {
        return dashboardService.countCountries();
    }

    @GetMapping("/medals")
    @Operation(summary = "Nombre total de médailles attribuées (Or, Argent, Bronze)")
    public MedalTotalsResponse getMedals() {
        return dashboardService.getMedalTotals();
    }

    @GetMapping("/countries/ranking")
    @Operation(summary = "Classement des pays par points (Or=7, Argent=4, Bronze=1)")
    public List<CountryRankingEntry> getCountriesRanking() {
        return dashboardService.getCountryRanking();
    }

    @GetMapping("/countries/medalists")
    @Operation(summary = "Nombre d'athlètes médaillés distincts par pays")
    public List<CountryMedalistsEntry> getCountriesMedalists() {
        return dashboardService.getCountryMedalists();
    }
}
