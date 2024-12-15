package com.wallet.crypto_performance.infra;

import com.wallet.crypto_performance.service.WalletService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/// Initialize the wallet on application startup
@Component
public class WalletInitializer implements CommandLineRunner {

    private final WalletService walletService;

    public WalletInitializer(WalletService walletService) {
        this.walletService = walletService;
    }

    @Override
    public void run(String... args) {
        walletService.initializeWallet();
    }
}
