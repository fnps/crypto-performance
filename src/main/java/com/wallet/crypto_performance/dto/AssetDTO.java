package com.wallet.crypto_performance.dto;

import java.math.BigDecimal;

public record AssetDTO (String symbol, BigDecimal quantity, BigDecimal originalPrice) {

}
