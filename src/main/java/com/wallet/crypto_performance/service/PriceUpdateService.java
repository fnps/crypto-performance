package com.wallet.crypto_performance.service;

import com.wallet.crypto_performance.infra.ScheduleConfig;
import com.wallet.crypto_performance.model.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PriceUpdateService {
    private static final Logger log = LoggerFactory.getLogger(PriceUpdateService.class);
    private final WalletService walletService;
    private final CoinService coinService;
    // Scheduled thread executor for the periodic invocation of the price update
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    // Fixed thread pool for the price update request execution
    // TODO remove hardcoded number of threads
    private final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public PriceUpdateService(WalletService walletService, CoinService coinService, ScheduleConfig scheduleConfig) {
        this.walletService = walletService;
        this.coinService = coinService;
        // Schedule the task dynamically based on the period
        scheduler.scheduleAtFixedRate(this::updateAssetPrices, 0, scheduleConfig.getPeriod(), TimeUnit.MILLISECONDS);
    }

    public void updateAssetPrices() {
        List<Asset> assets = walletService.getAllAssets();
        for (Asset asset : assets) {
            threadPool.submit(() -> {
                log.info("Submitted request asset {}", asset.getCoinId());
                var price = coinService.getCoinCurrentPrice(asset.getCoinId());
                log.info("New price for asset {}: {}", asset.getCoinId(), price);
                asset.setCurrentPrice(price);
                walletService.updateAsset(asset);
            });
        }
    }

}
