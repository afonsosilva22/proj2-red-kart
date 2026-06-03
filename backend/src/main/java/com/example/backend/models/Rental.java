package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "planned_start_datetime", nullable = false)
    private Instant plannedStartDatetime;

    @Column(name = "planned_end_datetime", nullable = false)
    private Instant plannedEndDatetime;

    @Column(name = "actual_start_datetime")
    private Instant actualStartDatetime;

    @Column(name = "actual_end_datetime")
    private Instant actualEndDatetime;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @ColumnDefault("0")
    @Column(name = "discount", precision = 5, scale = 2)
    private BigDecimal discount;

    @Column(name = "complaint", length = Integer.MAX_VALUE)
    private String complaint;

    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @ColumnDefault("'scheduled'")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"rentals", "blacklistEntries", "payments"})
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({"rentals"})
    private Employee employee;

    @JsonIgnore
    @OneToMany(mappedBy = "rental")
    private Set<Payment> payments = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "rental")
    private Set<Race> races = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getPlannedStartDatetime() {
        return plannedStartDatetime;
    }

    public void setPlannedStartDatetime(Instant plannedStartDatetime) {
        this.plannedStartDatetime = plannedStartDatetime;
    }

    public Instant getPlannedEndDatetime() {
        return plannedEndDatetime;
    }

    public void setPlannedEndDatetime(Instant plannedEndDatetime) {
        this.plannedEndDatetime = plannedEndDatetime;
    }

    public Instant getActualStartDatetime() {
        return actualStartDatetime;
    }

    public void setActualStartDatetime(Instant actualStartDatetime) {
        this.actualStartDatetime = actualStartDatetime;
    }

    public Instant getActualEndDatetime() {
        return actualEndDatetime;
    }

    public void setActualEndDatetime(Instant actualEndDatetime) {
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

    public Set<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    public Set<Race> getRaces() {
        return races;
    }

    public void setRaces(Set<Race> races) {
        this.races = races;
    }

}