package com.example.desktop.models;

public class Maintenance {
    private Integer id;
    private String openDate;
    private String completionDate;
    private String priority;
    private String description;
    private String type;
    private String status;
    private Track track;
    private Kart kart;
    private Employee employee;

    public Maintenance() {}

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getOpenDate() { return openDate; }

    public void setOpenDate(String openDate) { this.openDate = openDate; }

    public String getCompletionDate() { return completionDate; }

    public void setCompletionDate(String completionDate) { this.completionDate = completionDate; }

    public String getPriority() { return priority; }

    public void setPriority(String priority) { this.priority = priority; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Track getTrack() { return track; }

    public void setTrack(Track track) { this.track = track; }

    public Kart getKart() { return kart; }

    public void setKart(Kart kart) { this.kart = kart; }

    public Employee getEmployee() { return employee; }

    public void setEmployee(Employee employee) { this.employee = employee; }
}