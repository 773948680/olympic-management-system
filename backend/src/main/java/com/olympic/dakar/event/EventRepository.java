package com.olympic.dakar.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    /**
     * Surcharge avec fetch join sur la discipline : évite le N+1 lors du
     * mapping vers EventResponse.
     */
    @Override
    @EntityGraph(attributePaths = "discipline")
    Page<Event> findAll(Specification<Event> spec, Pageable pageable);
}
