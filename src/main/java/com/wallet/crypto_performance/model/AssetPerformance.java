package com.wallet.crypto_performance.model;

import com.wallet.crypto_performance.dto.AssetsPerformanceDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "performances",
        uniqueConstraints = {@UniqueConstraint(columnNames = "datePerformance")},
        indexes = {@Index(name = "idx_date_performance", columnList = "datePerformance")}
)
public class AssetPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal total;
    @Column(nullable = false)
    private String bestAsset;
    @Column(nullable = false)
    private BigDecimal bestPerformance;
    @Column(nullable = false)
    private String worstAsset;
    @Column(nullable = false)
    private BigDecimal worstPerformance;
    @Column(nullable = false)
    private LocalDate datePerformance;

    // Constructors
    public AssetPerformance() {
    }

    public AssetPerformance(BigDecimal total, String bestAsset, BigDecimal bestPerformance,
                            String worstAsset, BigDecimal worstPerformance, LocalDate datePerformance) {
        this.total = total;
        this.bestAsset = bestAsset;
        this.bestPerformance = bestPerformance;
        this.worstAsset = worstAsset;
        this.worstPerformance = worstPerformance;
        this.datePerformance = datePerformance;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getBestAsset() {
        return bestAsset;
    }

    public void setBestAsset(String bestAsset) {
        this.bestAsset = bestAsset;
    }

    public BigDecimal getBestPerformance() {
        return bestPerformance;
    }

    public void setBestPerformance(BigDecimal bestPerformance) {
        this.bestPerformance = bestPerformance;
    }

    public String getWorstAsset() {
        return worstAsset;
    }

    public void setWorstAsset(String worstAsset) {
        this.worstAsset = worstAsset;
    }

    public BigDecimal getWorstPerformance() {
        return worstPerformance;
    }

    public void setWorstPerformance(BigDecimal worstPerformance) {
        this.worstPerformance = worstPerformance;
    }

    public LocalDate getDatePerformance() {
        return datePerformance;
    }

    public void setDatePerformance(LocalDate datePerformance) {
        this.datePerformance = datePerformance;
    }

    public AssetsPerformanceDTO toDto() {
        return new AssetsPerformanceDTO(total, bestAsset, bestPerformance, worstAsset, worstPerformance);
    }
}
