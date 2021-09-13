package org.apache.fineract.operations;

import org.apache.fineract.organisation.parent.AbstractPersistableCustom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "m_charge", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "name") })
public class Charge extends AbstractPersistableCustom<Long> {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "charge_applies_to", nullable = false)
    private String chargeAppliesTo;

    @Column(name = "charge_calculation_enum")
    private Integer chargeCalculation;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "min_cap", scale = 6, precision = 19, nullable = true)
    private BigDecimal minCap;

    @Column(name = "max_cap", scale = 6, precision = 19, nullable = true)
    private BigDecimal maxCap;

    @Column(name = "fee_frequency", nullable = true)
    private Integer feeFrequency;

    public Charge() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getChargeAppliesTo() {
        return chargeAppliesTo;
    }

    public void setChargeAppliesTo(String chargeAppliesTo) {
        this.chargeAppliesTo = chargeAppliesTo;
    }

    public Integer getChargeCalculation() {
        return chargeCalculation;
    }

    public void setChargeCalculation(Integer chargeCalculation) {
        this.chargeCalculation = chargeCalculation;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public BigDecimal getMinCap() {
        return minCap;
    }

    public void setMinCap(BigDecimal minCap) {
        this.minCap = minCap;
    }

    public BigDecimal getMaxCap() {
        return maxCap;
    }

    public void setMaxCap(BigDecimal maxCap) {
        this.maxCap = maxCap;
    }

    public Integer getFeeFrequency() {
        return feeFrequency;
    }

    public void setFeeFrequency(Integer feeFrequency) {
        this.feeFrequency = feeFrequency;
    }
}
