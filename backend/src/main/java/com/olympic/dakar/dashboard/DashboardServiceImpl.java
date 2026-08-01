package com.olympic.dakar.dashboard;

import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.dashboard.dto.CountResponse;
import com.olympic.dakar.dashboard.dto.CountryMedalistsEntry;
import com.olympic.dakar.dashboard.dto.CountryRankingEntry;
import com.olympic.dakar.dashboard.dto.MedalTotalsResponse;
import com.olympic.dakar.medal.MedalTableService;
import com.olympic.dakar.result.MedalCount;
import com.olympic.dakar.result.ResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final long GOLD_POINTS = 7;
    private static final long SILVER_POINTS = 4;
    private static final long BRONZE_POINTS = 1;

    private final AthleteRepository athleteRepository;
    private final ResultRepository resultRepository;
    private final MedalTableService medalTableService;

    public DashboardServiceImpl(AthleteRepository athleteRepository, ResultRepository resultRepository,
                                 MedalTableService medalTableService) {
        this.athleteRepository = athleteRepository;
        this.resultRepository = resultRepository;
        this.medalTableService = medalTableService;
    }

    @Override
    public CountResponse countAthletes() {
        return new CountResponse(athleteRepository.count());
    }

    @Override
    public CountResponse countCountries() {
        return new CountResponse(athleteRepository.countDistinctNationalities());
    }

    @Override
    public MedalTotalsResponse getMedalTotals() {
        long gold = 0;
        long silver = 0;
        long bronze = 0;
        for (MedalCount row : resultRepository.countByMedalType()) {
            switch (row.medal()) {
                case GOLD -> gold = row.count();
                case SILVER -> silver = row.count();
                case BRONZE -> bronze = row.count();
                case NONE -> {
                }
            }
        }
        return new MedalTotalsResponse(gold, silver, bronze, gold + silver + bronze);
    }

    @Override
    public List<CountryRankingEntry> getCountryRanking() {
        return medalTableService.getMedalTable().stream()
                .map(entry -> new CountryRankingEntry(
                        entry.nationality(),
                        entry.gold(),
                        entry.silver(),
                        entry.bronze(),
                        entry.gold() * GOLD_POINTS + entry.silver() * SILVER_POINTS + entry.bronze() * BRONZE_POINTS))
                .sorted(Comparator.comparingLong(CountryRankingEntry::points).reversed()
                        .thenComparing(CountryRankingEntry::nationality))
                .toList();
    }

    @Override
    public List<CountryMedalistsEntry> getCountryMedalists() {
        return resultRepository.countMedalistsByNationality().stream()
                .map(row -> new CountryMedalistsEntry(row.nationality(), row.count()))
                .sorted(Comparator.comparingLong(CountryMedalistsEntry::medalists).reversed()
                        .thenComparing(CountryMedalistsEntry::nationality))
                .toList();
    }
}
