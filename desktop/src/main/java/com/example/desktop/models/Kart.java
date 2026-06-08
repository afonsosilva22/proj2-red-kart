package com.example.desktop.models;

public class Kart {
    private Integer id;
    private String kartNumber;
    private Integer mileage;
    private Integer manufactureYear;
    private String lastServiceDate; // Represented as String for seamless GSON parsing
    private String status;
    private KartTypePrice type;

    public Kart() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKartNumber() {
        return kartNumber;
    }

    public void setKartNumber(String kartNumber) {
        this.kartNumber = kartNumber;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public String getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(String lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public KartTypePrice getType() {
        return type;
    }

    public void setType(KartTypePrice type) {
        this.type = type;
    }

    // Convenience UI wrapper to read nested type values directly inside TableColumns
    public String getKartTypeName() {
        return type != null ? type.getType() : "N/A";
    }

    // Convenience UI wrapper to get formatted price streams directly
    public String getPricePerHourDisplay() {
        return (type != null && type.getPricePerHour() != null)
                ? type.getPricePerHour().toString() + " €"
                : "0.00 €";
    }
}