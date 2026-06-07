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
    private Employee employee;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlannedStartDatetime() {
        return plannedStartDatetime;
    }

    public void setPlannedStartDatetime(String plannedStartDatetime) {
        this.plannedStartDatetime = plannedStartDatetime;
    }

    public String getPlannedEndDatetime() {
        return plannedEndDatetime;
    }

    public void setPlannedEndDatetime(String plannedEndDatetime) {
        this.plannedEndDatetime = plannedEndDatetime;
    }

    public String getActualStartDatetime() {
        return actualStartDatetime;
    }

    public void setActualStartDatetime(String actualStartDatetime) {
        this.actualStartDatetime = actualStartDatetime;
    }

    public String getActualEndDatetime() {
        return actualEndDatetime;
    }

    public void setActualEndDatetime(String actualEndDatetime) {
        this.actualEndDatetime = actualEndDatetime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}