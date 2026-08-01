package com.olympic.dakar.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    /**
     * Fetch join athlete+event : évite le N+1 (un SELECT athlète par résultat)
     * lors du mapping vers ResultResponse.
     */
    @Query("""
            SELECT r FROM Result r
            JOIN FETCH r.athlete
            JOIN FETCH r.event
            WHERE r.event.id = :eventId
            ORDER BY r.position ASC
            """)
    List<Result> findByEventIdOrderByPositionAsc(@Param("eventId") Long eventId);

    boolean existsByEventIdAndAthleteId(Long eventId, Long athleteId);

    boolean existsByEventIdAndPosition(Long eventId, Integer position);

    Optional<Result> findByEventIdAndAthleteId(Long eventId, Long athleteId);

    Optional<Result> findByEventIdAndPosition(Long eventId, Integer position);

    @Query("""
            SELECT r FROM Result r
            JOIN FETCH r.athlete
            JOIN FETCH r.event
            WHERE r.athlete.id = :athleteId
            ORDER BY r.event.eventDate DESC
            """)
    List<Result> findByAthleteId(@Param("athleteId") Long athleteId);

    @Query("""
            SELECT r FROM Result r
            JOIN FETCH r.athlete a
            JOIN FETCH r.event e
            WHERE a.nationality = :nationality AND r.medal <> com.olympic.dakar.result.MedalType.NONE
            ORDER BY e.eventDate DESC
            """)
    List<Result> findMedalHistoryByNationality(@Param("nationality") String nationality);

    /**
     * Agrégation calculée côté base (GROUP BY) : au plus (nb pays × 3) lignes,
     * jamais le contenu complet de la table results.
     */
    @Query("""
            SELECT new com.olympic.dakar.result.NationalityMedalCount(a.nationality, r.medal, COUNT(r))
            FROM Result r JOIN r.athlete a
            WHERE r.medal <> com.olympic.dakar.result.MedalType.NONE
            GROUP BY a.nationality, r.medal
            """)
    List<NationalityMedalCount> countMedalsByNationality();

    @Query("""
            SELECT new com.olympic.dakar.result.NationalityCount(a.nationality, COUNT(DISTINCT a.id))
            FROM Result r JOIN r.athlete a
            WHERE r.medal <> com.olympic.dakar.result.MedalType.NONE
            GROUP BY a.nationality
            """)
    List<NationalityCount> countMedalistsByNationality();

    @Query("""
            SELECT new com.olympic.dakar.result.MedalCount(r.medal, COUNT(r))
            FROM Result r
            WHERE r.medal <> com.olympic.dakar.result.MedalType.NONE
            GROUP BY r.medal
            """)
    List<MedalCount> countByMedalType();
}
