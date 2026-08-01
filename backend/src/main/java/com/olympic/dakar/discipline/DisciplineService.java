package com.olympic.dakar.discipline;

import com.olympic.dakar.discipline.dto.DisciplinePatchRequest;
import com.olympic.dakar.discipline.dto.DisciplineRequest;
import com.olympic.dakar.discipline.dto.DisciplineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DisciplineService {

    Page<DisciplineResponse> findAll(Pageable pageable);

    DisciplineResponse findById(Long id);

    DisciplineResponse create(DisciplineRequest request);

    DisciplineResponse update(Long id, DisciplineRequest request);

    DisciplineResponse patch(Long id, DisciplinePatchRequest request);

    void delete(Long id);
}
