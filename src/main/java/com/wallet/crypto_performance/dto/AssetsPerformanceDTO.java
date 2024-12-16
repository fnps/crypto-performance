package com.wallet.crypto_performance.dto;

import java.math.BigDecimal;

public record AssetsPerformanceDTO (BigDecimal total, String best_asset, BigDecimal best_performance, String worst_asset, BigDecimal worst_performance) {
}
