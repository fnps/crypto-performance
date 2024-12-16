package com.wallet.crypto_performance.exception;

public class UnknownAssetPriceException extends RuntimeException {
    public UnknownAssetPriceException(String message) {
        super(message);
    }
}
