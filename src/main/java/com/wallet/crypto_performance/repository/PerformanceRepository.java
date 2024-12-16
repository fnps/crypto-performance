package com.wallet.crypto_performance.repository;

import com.wallet.crypto_performance.model.AssetPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<AssetPerformance, Long> {
    Optional<AssetPerformance> findByDatePerformance(LocalDate datePerformance);
}
