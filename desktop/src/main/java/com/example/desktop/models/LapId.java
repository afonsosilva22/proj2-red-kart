package com.example.desktop.models;

import java.io.Serializable;
import java.util.Objects;

public class LapId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer raceId;
    private Integer kartId;
    private Integer lapNumber;

    public LapId() {}

    public LapId(Integer raceId, Integer kartId, Integer lapNumber) {
        this.raceId = raceId;
        this.kartId = kartId;
        this.lapNumber = lapNumber;
    }

    public Integer getRaceId() { return raceId; }

    public void setRaceId(Integer raceId) { this.raceId = raceId; }

    public Integer getKartId() { return kartId; }

    public void setKartId(Integer kartId) { this.kartId = kartId; }

    public Integer getLapNumber() { return lapNumber; }

    public void setLapNumber(Integer lapNumber) { this.lapNumber = lapNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LapId lapId = (LapId) o;
        return Objects.equals(raceId, lapId.raceId) &&
                Objects.equals(kartId, lapId.kartId) &&
                Objects.equals(lapNumber, lapId.lapNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raceId, kartId, lapNumber);
    }
}