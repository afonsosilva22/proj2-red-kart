package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "race_kart")
public class RaceKart {
    @EmbeddedId
    private RaceKartId id;

    @MapsId("raceId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @MapsId("kartId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "kart_id", nullable = false)
    private Kart kart;

    @OneToMany(mappedBy = "raceKart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lap> laps = new LinkedHashSet<>();

    public RaceKartId getId() {
        return id;
    }

    public void setId(RaceKartId id) {
        this.id = id;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Kart getKart() {
        return kart;
    }

    public void setKart(Kart kart) {
        this.kart = kart;
    }

    public Set<Lap> getLaps() {
        return laps;
    }

    public void setLaps(Set<Lap> laps) {
        this.laps = laps;
    }

}