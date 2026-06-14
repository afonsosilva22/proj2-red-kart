package com.example.desktop.models;

import com.google.gson.annotations.Expose;

import java.util.LinkedHashSet;
import java.util.Set;

public class RaceKart {
    private RaceKartId id;
    @Expose(serialize = false)
    private transient Race race;
    private Kart kart;
    private Set<Lap> laps = new LinkedHashSet<>();

    public RaceKartId getId() { return id; }

    public void setId(RaceKartId id) { this.id = id; }

    public Race getRace() { return race; }

    public void setRace(Race race) { this.race = race; }

    public Kart getKart() { return kart; }

    public void setKart(Kart kart) { this.kart = kart; }

    public Set<Lap> getLaps() { return laps; }

    public void setLaps(Set<Lap> laps) { this.laps = laps; }
}