package com.example.desktop.models;

public class RaceEquipment {
    private RaceEquipmentId id;
    private transient Race race;
    private Equipment equipment;
    private Integer quantity = 1;

    public RaceEquipmentId getId() { return id; }

    public void setId(RaceEquipmentId id) { this.id = id; }

    public Race getRace() { return race; }

    public void setRace(Race race) { this.race = race; }

    public Equipment getEquipment() { return equipment; }

    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}