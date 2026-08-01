package com.olympic.dakar.result;

import com.olympic.dakar.athlete.Athlete;
import com.olympic.dakar.event.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "results", uniqueConstraints = {
        @UniqueConstraint(name = "uk_result_event_athlete", columnNames = {"event_id", "athlete_id"}),
        @UniqueConstraint(name = "uk_result_event_position", columnNames = {"event_id", "position_value"})
})
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @Column(name = "position_value", nullable = false)
    private Integer position;

    @Column(name = "time_value", length = 50)
    private String time;

    @Column(name = "score_value")
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MedalType medal;

    protected Result() {
    }

    public Result(Event event, Athlete athlete, Integer position, String time, Double score) {
        this.event = event;
        this.athlete = athlete;
        this.position = position;
        this.time = time;
        this.score = score;
        this.medal = MedalCalculator.forPosition(position);
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
        this.medal = MedalCalculator.forPosition(position);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public MedalType getMedal() {
        return medal;
    }
}
