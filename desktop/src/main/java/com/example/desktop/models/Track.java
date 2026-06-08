package com.example.desktop.models;

public class Track {
    private Integer id;
    private String name;
    private Double pricePerHour;
    private Double lengthKm;
    private Integer kartLimit;
    private String status;

    public Track() {}

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Double getPricePerHour() { return pricePerHour; }

    public void setPricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; }

    public Double getLengthKm() { return lengthKm; }

    public void setLengthKm(Double lengthKm) { this.lengthKm = lengthKm; }

    public Integer getKartLimit() { return kartLimit; }

    public void setKartLimit(Integer kartLimit) { this.kartLimit = kartLimit; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}