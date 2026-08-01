package com.olympic.dakar.medal;

import com.olympic.dakar.medal.dto.MedalTableEntry;
import com.olympic.dakar.result.NationalityMedalCount;
import com.olympic.dakar.result.ResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class MedalTableServiceImpl implements MedalTableService {

    private final ResultRepository resultRepository;

    public MedalTableServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public List<MedalTableEntry> getMedalTable() {
        Map<String, long[]> countsByNationality = new LinkedHashMap<>();

        for (NationalityMedalCount row : resultRepository.countMedalsByNationality()) {
            long[] counts = countsByNationality.computeIfAbsent(row.nationality(), key -> new long[3]);
            switch (row.medal()) {
                case GOLD -> counts[0] += row.count();
                case SILVER -> counts[1] += row.count();
                case BRONZE -> counts[2] += row.count();
                case NONE -> {
                }
            }
        }

        return countsByNationality.entrySet().stream()
                .map(entry -> {
                    long gold = entry.getValue()[0];
                    long silver = entry.getValue()[1];
                    long bronze = entry.getValue()[2];
                    return new MedalTableEntry(entry.getKey(), gold, silver, bronze, gold + silver + bronze);
                })
                .sorted(Comparator.comparingLong(MedalTableEntry::gold).reversed()
                        .thenComparing(Comparator.comparingLong(MedalTableEntry::silver).reversed())
                        .thenComparing(Comparator.comparingLong(MedalTableEntry::bronze).reversed())
                        .thenComparing(MedalTableEntry::nationality))
                .toList();
    }
}
