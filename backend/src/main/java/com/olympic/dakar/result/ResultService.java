package com.olympic.dakar.result;

import com.olympic.dakar.result.dto.ResultRequest;
import com.olympic.dakar.result.dto.ResultResponse;

import java.util.List;

public interface ResultService {

    List<ResultResponse> findByEvent(Long eventId);

    List<ResultResponse> findByAthlete(Long athleteId);

    List<ResultResponse> findMedalHistoryByNationality(String nationality);

    List<ResultResponse> findPodium(Long eventId);

    ResultResponse findById(Long id);

    ResultResponse create(ResultRequest request);

    ResultResponse update(Long id, ResultRequest request);

    void delete(Long id);
}
