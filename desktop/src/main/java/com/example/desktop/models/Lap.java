package com.example.desktop.models;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;

public class Lap {
    private LapId id;
    @Expose(serialize = false)
    private RaceKart raceKart;
    private BigDecimal lapTime;

    public Lap() {}

    public Lap(LapId id, RaceKart raceKart, BigDecimal lapTime) {
        this.id = id;
        this.raceKart = raceKart;
        this.lapTime = lapTime;
    }

    public LapId getId() { return id; }

    public void setId(LapId id) { this.id = id; }

    public RaceKart getRaceKart() { return raceKart; }

    public void setRaceKart(RaceKart raceKart) { this.raceKart = raceKart; }

    public BigDecimal getLapTime() { return lapTime; }

    public void setLapTime(BigDecimal lapTime) { this.lapTime = lapTime; }
}