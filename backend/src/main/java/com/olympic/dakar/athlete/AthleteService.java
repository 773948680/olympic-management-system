package com.olympic.dakar.athlete;

import com.olympic.dakar.athlete.dto.AthletePatchRequest;
import com.olympic.dakar.athlete.dto.AthleteRequest;
import com.olympic.dakar.athlete.dto.AthleteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AthleteService {

    Page<AthleteResponse> search(String lastName, String firstName, Gender gender, String nationality,
                                  Long disciplineId, LocalDate bornAfter, LocalDate bornBefore, Pageable pageable);

    Page<AthleteResponse> findByDiscipline(Long disciplineId, Pageable pageable);

    AthleteResponse findById(Long id);

    AthleteResponse create(AthleteRequest request);

    AthleteResponse update(Long id, AthleteRequest request);

    AthleteResponse patch(Long id, AthletePatchRequest request);

    void delete(Long id);
}
