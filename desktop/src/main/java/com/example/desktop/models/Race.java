package com.example.desktop.models;

import java.util.LinkedHashSet;
import java.util.Set;

public class Race {
    private Integer id;
    private String startDatetime;
    private String endDatetime;
    private String status = "scheduled";
    private Rental rental;
    private Employee employee;
    private Track track;

    private Set<RaceEquipment> raceEquipments = new LinkedHashSet<>();
    private Set<RaceKart> raceKarts = new LinkedHashSet<>();

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getStartDatetime() { return startDatetime; }

    public void setStartDatetime(String startDatetime) { this.startDatetime = startDatetime; }

    public String getEndDatetime() { return endDatetime; }

    public void setEndDatetime(String endDatetime) { this.endDatetime = endDatetime; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Rental getRental() { return rental; }

    public void setRental(Rental rental) { this.rental = rental; }

    public Employee getEmployee() { return employee; }

    public void setEmployee(Employee employee) { this.employee = employee; }

    public Track getTrack() { return track; }

    public void setTrack(Track track) { this.track = track; }

    public Set<RaceEquipment> getRaceEquipments() { return raceEquipments; }

    public void setRaceEquipments(Set<RaceEquipment> raceEquipments) { this.raceEquipments = raceEquipments; }

    public Set<RaceKart> getRaceKarts() { return raceKarts; }

    public void setRaceKarts(Set<RaceKart> raceKarts) { this.raceKarts = raceKarts; }
}