package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lap")
public class Lap {
    @EmbeddedId
    private LapId id;

    @JoinColumns({
            @JoinColumn(name = "race_id", referencedColumnName = "race_id", insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = "kart_id", referencedColumnName = "kart_id", insertable = false, updatable = false, nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private RaceKart raceKart;

    @Column(name = "lap_time", nullable = false, precision = 6, scale = 3)
    private BigDecimal lapTime;

    public LapId getId() {
        return id;
    }

    public void setId(LapId id) {
        this.id = id;
    }

    public RaceKart getRaceKart() {
        return raceKart;
    }

    public void setRaceKart(RaceKart raceKart) {
        this.raceKart = raceKart;
    }

    public BigDecimal getLapTime() {
        return lapTime;
    }

    public void setLapTime(BigDecimal lapTime) {
        this.lapTime = lapTime;
    }
}