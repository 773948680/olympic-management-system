package com.olympic.dakar.medal;

import com.olympic.dakar.medal.dto.MedalTableEntry;
import com.olympic.dakar.result.MedalType;
import com.olympic.dakar.result.NationalityMedalCount;
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
class MedalTableServiceImplTest {

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private MedalTableServiceImpl medalTableService;

    @Test
    void medalTableShouldBeSortedByGoldThenSilverThenBronze() {
        List<NationalityMedalCount> rows = List.of(
                new NationalityMedalCount("A", MedalType.GOLD, 2L),
                new NationalityMedalCount("A", MedalType.SILVER, 1L),
                new NationalityMedalCount("B", MedalType.GOLD, 2L),
                new NationalityMedalCount("B", MedalType.BRONZE, 1L),
                new NationalityMedalCount("C", MedalType.GOLD, 1L),
                new NationalityMedalCount("C", MedalType.SILVER, 3L),
                new NationalityMedalCount("D", MedalType.BRONZE, 1L)
        );
        when(resultRepository.countMedalsByNationality()).thenReturn(rows);

        List<MedalTableEntry> table = medalTableService.getMedalTable();

        assertThat(table).extracting(MedalTableEntry::nationality)
                .containsExactly("A", "B", "C", "D");

        MedalTableEntry nationA = table.get(0);
        assertThat(nationA.gold()).isEqualTo(2);
        assertThat(nationA.silver()).isEqualTo(1);
        assertThat(nationA.bronze()).isEqualTo(0);
        assertThat(nationA.total()).isEqualTo(3);

        MedalTableEntry nationB = table.get(1);
        assertThat(nationB.gold()).isEqualTo(2);
        assertThat(nationB.silver()).isEqualTo(0);
        assertThat(nationB.bronze()).isEqualTo(1);
    }

    @Test
    void medalTableShouldBeEmptyWhenNoMedalsAwarded() {
        when(resultRepository.countMedalsByNationality()).thenReturn(List.of());

        assertThat(medalTableService.getMedalTable()).isEmpty();
    }
}
