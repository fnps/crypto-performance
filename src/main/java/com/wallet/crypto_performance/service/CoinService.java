package com.wallet.crypto_performance.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public interface CoinService {
    Optional<String> getCoinIdBySymbol(String symbol);
    Optional<BigDecimal> getCoinCurrentPrice(String coinId);
    Optional<BigDecimal> getCoinPreviousPrice(String coinId, LocalDate pastDate);

}
