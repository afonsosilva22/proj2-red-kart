package com.example.desktop.models;

import java.math.BigDecimal;

public class Rental {
    private Integer id;
    private String plannedStartDatetime;
    private String plannedEndDatetime;
    private String actualStartDatetime;
    private String actualEndDatetime;
    private BigDecimal basePrice;
    private BigDecimal discount;
    private String complaint;
    private String type;
    private String status;
    private Customer customer;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getPlannedStartDatetime() { return plannedStartDatetime; }

    public void setPlannedStartDatetime(String plannedStartDatetime) { this.plannedStartDatetime = plannedStartDatetime; }

    public String getPlannedEndDatetime() { return plannedEndDatetime; }

    public void setPlannedEndDatetime(String plannedEndDatetime) { this.plannedEndDatetime = plannedEndDatetime; }

    public BigDecimal getBasePrice() { return basePrice; }

    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }
}