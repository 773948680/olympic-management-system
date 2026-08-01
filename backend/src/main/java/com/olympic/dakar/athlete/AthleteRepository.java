package com.olympic.dakar.athlete;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;

public interface AthleteRepository extends JpaRepository<Athlete, Long>, JpaSpecificationExecutor<Athlete> {

    /**
     * Surcharge avec fetch join sur la discipline : évite le N+1 (une requête
     * de discipline par athlète) lors du mapping vers AthleteResponse.
     */
    @Override
    @EntityGraph(attributePaths = "discipline")
    Page<Athlete> findAll(Specification<Athlete> spec, Pageable pageable);

    @EntityGraph(attributePaths = "discipline")
    Page<Athlete> findByDisciplineId(Long disciplineId, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT a.nationality) FROM Athlete a")
    long countDistinctNationalities();
}
