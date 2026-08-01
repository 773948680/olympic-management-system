package com.olympic.dakar.discipline;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisciplineRepository extends JpaRepository<Discipline, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Discipline> findByNameIgnoreCase(String name);
}
