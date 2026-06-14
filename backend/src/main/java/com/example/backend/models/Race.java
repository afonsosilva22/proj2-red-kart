package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "race")
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "start_datetime")
    private Instant startDatetime;

    @Column(name = "end_datetime")
    private Instant endDatetime;

    @ColumnDefault("'scheduled'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @JsonIgnoreProperties("race")
    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RaceKart> raceKarts = new LinkedHashSet<>();

    @JsonIgnoreProperties("race")
    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RaceEquipment> raceEquipments = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Instant startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Instant getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Instant endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Set<RaceKart> getRaceKarts() {
        return raceKarts;
    }

    public void setRaceKarts(Set<RaceKart> raceKarts) {
        this.raceKarts = raceKarts;
    }

    public Set<RaceEquipment> getRaceEquipments() {
        return raceEquipments;
    }

    public void setRaceEquipments(Set<RaceEquipment> raceEquipments) {
        this.raceEquipments = raceEquipments;
    }

}