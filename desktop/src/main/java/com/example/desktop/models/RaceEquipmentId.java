package com.example.desktop.models;

import java.io.Serializable;
import java.util.Objects;

public class RaceEquipmentId implements Serializable {
    private Integer raceId;
    private Integer equipmentId;

    public RaceEquipmentId() {}

    public RaceEquipmentId(Integer raceId, Integer equipmentId) {
        this.raceId = raceId;
        this.equipmentId = equipmentId;
    }

    public Integer getRaceId() { return raceId; }

    public void setRaceId(Integer raceId) { this.raceId = raceId; }

    public Integer getEquipmentId() { return equipmentId; }

    public void setEquipmentId(Integer equipmentId) { this.equipmentId = equipmentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaceEquipmentId that = (RaceEquipmentId) o;
        return Objects.equals(raceId, that.raceId) && Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raceId, equipmentId);
    }
}