package com.olympic.dakar.dashboard;

import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.dashboard.dto.CountResponse;
import com.olympic.dakar.dashboard.dto.CountryMedalistsEntry;
import com.olympic.dakar.dashboard.dto.CountryRankingEntry;
import com.olympic.dakar.dashboard.dto.MedalTotalsResponse;
import com.olympic.dakar.medal.MedalTableService;
import com.olympic.dakar.medal.dto.MedalTableEntry;
import com.olympic.dakar.result.MedalCount;
import com.olympic.dakar.result.MedalType;
import com.olympic.dakar.result.NationalityCount;
import com.olympic.dakar.result.ResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private AthleteRepository athleteRepository;
    @Mock
    private ResultRepository resultRepository;
    @Mock
    private MedalTableService medalTableService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void countAthletesShouldDelegateToRepositoryCount() {
        when(athleteRepository.count()).thenReturn(42L);

        CountResponse response = dashboardService.countAthletes();

        assertThat(response.count()).isEqualTo(42L);
    }

    @Test
    void countCountriesShouldDelegateToRepository() {
        when(athleteRepository.countDistinctNationalities()).thenReturn(17L);

        CountResponse response = dashboardService.countCountries();

        assertThat(response.count()).isEqualTo(17L);
    }

    @Test
    void getMedalTotalsShouldAggregateAllMedalTypes() {
        when(resultRepository.countByMedalType()).thenReturn(List.of(
                new MedalCount(MedalType.GOLD, 5L),
                new MedalCount(MedalType.SILVER, 3L),
                new MedalCount(MedalType.BRONZE, 2L)
        ));

        MedalTotalsResponse response = dashboardService.getMedalTotals();

        assertThat(response.gold()).isEqualTo(5L);
        assertThat(response.silver()).isEqualTo(3L);
        assertThat(response.bronze()).isEqualTo(2L);
        assertThat(response.total()).isEqualTo(10L);
    }

    @Test
    void getMedalTotalsShouldDefaultMissingMedalTypesToZero() {
        when(resultRepository.countByMedalType()).thenReturn(List.of(new MedalCount(MedalType.GOLD, 3L)));

        MedalTotalsResponse response = dashboardService.getMedalTotals();

        assertThat(response.gold()).isEqualTo(3L);
        assertThat(response.silver()).isZero();
        assertThat(response.bronze()).isZero();
        assertThat(response.total()).isEqualTo(3L);
    }

    @Test
    void getMedalTotalsShouldBeAllZeroWhenNoResults() {
        when(resultRepository.countByMedalType()).thenReturn(List.of());

        MedalTotalsResponse response = dashboardService.getMedalTotals();

        assertThat(response.gold()).isZero();
        assertThat(response.silver()).isZero();
        assertThat(response.bronze()).isZero();
        assertThat(response.total()).isZero();
    }

    @Test
    void getCountryRankingShouldRankMoreSilverAboveFewerGoldWhenPointsHigher() {
        when(medalTableService.getMedalTable()).thenReturn(List.of(
                new MedalTableEntry("A", 1, 0, 0, 1),
                new MedalTableEntry("B", 0, 2, 0, 2)
        ));

        List<CountryRankingEntry> ranking = dashboardService.getCountryRanking();

        assertThat(ranking).extracting(CountryRankingEntry::nationality).containsExactly("B", "A");
        assertThat(ranking.get(0).points()).isEqualTo(8L);
        assertThat(ranking.get(1).points()).isEqualTo(7L);
    }

    @Test
    void getCountryRankingShouldComputePointsUsingGold7Silver4Bronze1() {
        when(medalTableService.getMedalTable()).thenReturn(List.of(
                new MedalTableEntry("A", 2, 1, 3, 6)
        ));

        List<CountryRankingEntry> ranking = dashboardService.getCountryRanking();

        assertThat(ranking).hasSize(1);
        assertThat(ranking.get(0).points()).isEqualTo(2 * 7 + 1 * 4 + 3 * 1);
    }

    @Test
    void getCountryMedalistsShouldMapAndSortDescending() {
        when(resultRepository.countMedalistsByNationality()).thenReturn(List.of(
                new NationalityCount("A", 2L),
                new NationalityCount("B", 5L)
        ));

        List<CountryMedalistsEntry> medalists = dashboardService.getCountryMedalists();

        assertThat(medalists).extracting(CountryMedalistsEntry::nationality).containsExactly("B", "A");
        assertThat(medalists.get(0).medalists()).isEqualTo(5L);
    }
}
