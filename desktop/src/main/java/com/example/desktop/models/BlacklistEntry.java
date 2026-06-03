package com.example.desktop.models;

public class BlacklistEntry {

    private Integer id;
    private String entryReason;
    private String entryDate;
    private String exitDate;
    private Customer customer;

    public BlacklistEntry() {
    }

    public BlacklistEntry(String entryReason, String entryDate, Customer customer) {
        this.entryReason = entryReason;
        this.entryDate = entryDate;
        this.customer = customer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntryReason() {
        return entryReason;
    }

    public void setEntryReason(String entryReason) {
        this.entryReason = entryReason;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getExitDate() {
        return exitDate;
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}