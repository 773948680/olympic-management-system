package com.olympic.dakar.dashboard;

import com.olympic.dakar.dashboard.dto.CountResponse;
import com.olympic.dakar.dashboard.dto.CountryMedalistsEntry;
import com.olympic.dakar.dashboard.dto.CountryRankingEntry;
import com.olympic.dakar.dashboard.dto.MedalTotalsResponse;

import java.util.List;

public interface DashboardService {

    CountResponse countAthletes();

    CountResponse countCountries();

    MedalTotalsResponse getMedalTotals();

    List<CountryRankingEntry> getCountryRanking();

    List<CountryMedalistsEntry> getCountryMedalists();
}
