package com.olympic.dakar.event;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class EventSpecifications {

    private EventSpecifications() {
    }

    public static Specification<Event> withCriteria(Long disciplineId, LocalDate date) {
        return Specification
                .where(hasDisciplineId(disciplineId))
                .and(onDate(date));
    }

    private static Specification<Event> hasDisciplineId(Long disciplineId) {
        return (root, query, cb) -> disciplineId == null ? null :
                cb.equal(root.get("discipline").get("id"), disciplineId);
    }

    private static Specification<Event> onDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) {
                return null;
            }
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59, 999_999_999);
            return cb.between(root.get("eventDate"), start, end);
        };
    }
}
