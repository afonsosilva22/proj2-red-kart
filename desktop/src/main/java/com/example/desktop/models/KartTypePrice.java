package com.example.desktop.models;

import java.math.BigDecimal;

public class KartTypePrice {
    private String type;
    private BigDecimal pricePerHour;

    public KartTypePrice() {}

    public KartTypePrice(String type, BigDecimal pricePerHour) {
        this.type = type;
        this.pricePerHour = pricePerHour;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    // Overriding toString makes this populate beautifully inside UI ComboBoxes later
    @Override
    public String toString() {
        return type != null ? type : "";
    }
}