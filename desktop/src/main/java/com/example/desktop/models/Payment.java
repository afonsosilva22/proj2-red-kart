package com.example.desktop.models;

import java.math.BigDecimal;

public class Payment {
    private Integer id;
    private String paymentDate;
    private BigDecimal amountPaid;
    private BigDecimal ivaRate;
    private String paymentMethod;
    private Rental rental;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getPaymentDate() { return paymentDate; }

    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmountPaid() { return amountPaid; }

    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getIvaRate() { return ivaRate; }

    public void setIvaRate(BigDecimal ivaRate) { this.ivaRate = ivaRate; }

    public String getPaymentMethod() { return paymentMethod; }

    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Rental getRental() { return rental; }

    public void setRental(Rental rental) { this.rental = rental; }
}