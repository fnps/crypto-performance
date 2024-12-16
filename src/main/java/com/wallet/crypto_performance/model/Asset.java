package com.wallet.crypto_performance.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String coinId;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false, name = "original_price")
    private BigDecimal originalPrice;

    @Column(nullable = false, name = "current_price")
    private BigDecimal currentPrice;

    public Long getId() {
        return this.id;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public BigDecimal getOriginalPrice() {
        return this.originalPrice;
    }

    public BigDecimal getCurrentPrice() {
        return this.currentPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }
}
