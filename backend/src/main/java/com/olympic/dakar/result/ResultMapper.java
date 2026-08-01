package com.olympic.dakar.result;

import com.olympic.dakar.result.dto.ResultResponse;

public final class ResultMapper {

    private ResultMapper() {
    }

    public static ResultResponse toResponse(Result result) {
        return new ResultResponse(
                result.getId(),
                result.getEvent().getId(),
                result.getEvent().getName(),
                result.getAthlete().getId(),
                result.getAthlete().getFirstName(),
                result.getAthlete().getLastName(),
                result.getPosition(),
                result.getTime(),
                result.getScore(),
                result.getMedal()
        );
    }
}
