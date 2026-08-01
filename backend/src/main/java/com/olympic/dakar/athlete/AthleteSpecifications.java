package com.olympic.dakar.athlete;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class AthleteSpecifications {

    private AthleteSpecifications() {
    }

    public static Specification<Athlete> withCriteria(String lastName, String firstName, Gender gender,
                                                        String nationality, Long disciplineId,
                                                        LocalDate bornAfter, LocalDate bornBefore) {
        return Specification
                .where(lastNameContains(lastName))
                .and(firstNameContains(firstName))
                .and(hasGender(gender))
                .and(nationalityEquals(nationality))
                .and(hasDisciplineId(disciplineId))
                .and(bornAfter(bornAfter))
                .and(bornBefore(bornBefore));
    }

    private static Specification<Athlete> lastNameContains(String lastName) {
        return (root, query, cb) -> lastName == null ? null :
                cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    private static Specification<Athlete> firstNameContains(String firstName) {
        return (root, query, cb) -> firstName == null ? null :
                cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    private static Specification<Athlete> hasGender(Gender gender) {
        return (root, query, cb) -> gender == null ? null : cb.equal(root.get("gender"), gender);
    }

    private static Specification<Athlete> nationalityEquals(String nationality) {
        return (root, query, cb) -> nationality == null ? null :
                cb.equal(cb.lower(root.get("nationality")), nationality.toLowerCase());
    }

    private static Specification<Athlete> hasDisciplineId(Long disciplineId) {
        return (root, query, cb) -> disciplineId == null ? null :
                cb.equal(root.get("discipline").get("id"), disciplineId);
    }

    private static Specification<Athlete> bornAfter(LocalDate date) {
        return (root, query, cb) -> date == null ? null :
                cb.greaterThanOrEqualTo(root.get("dateOfBirth"), date);
    }

    private static Specification<Athlete> bornBefore(LocalDate date) {
        return (root, query, cb) -> date == null ? null :
                cb.lessThanOrEqualTo(root.get("dateOfBirth"), date);
    }
}
