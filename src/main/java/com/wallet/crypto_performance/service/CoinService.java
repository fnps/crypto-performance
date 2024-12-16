package com.wallet.crypto_performance.service;

import java.math.BigDecimal;
import java.time.Instant;

public interface CoinService {
    String getCoinIdBySymbol(String symbol);
    BigDecimal getCoinCurrentPrice(String coinId);
    BigDecimal getCoinPreviousPrice(Instant pastDate);

}
