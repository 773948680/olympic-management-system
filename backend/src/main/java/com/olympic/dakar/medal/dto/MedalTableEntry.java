package com.olympic.dakar.medal.dto;

public record MedalTableEntry(
        String nationality,
        long gold,
        long silver,
        long bronze,
        long total
) {
}
