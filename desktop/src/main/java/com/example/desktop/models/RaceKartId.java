package com.example.desktop.models;

import java.io.Serializable;
import java.util.Objects;

public class RaceKartId implements Serializable {
    private Integer raceId;
    private Integer kartId;

    public RaceKartId() {}

    public RaceKartId(Integer raceId, Integer kartId) {
        this.raceId = raceId;
        this.kartId = kartId;
    }

    public Integer getRaceId() { return raceId; }

    public void setRaceId(Integer raceId) { this.raceId = raceId; }

    public Integer getKartId() { return kartId; }

    public void setKartId(Integer kartId) { this.kartId = kartId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaceKartId raceKartId = (RaceKartId) o;
        return Objects.equals(raceId, raceKartId.raceId) && Objects.equals(kartId, raceKartId.kartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raceId, kartId);
    }
}