package com.wallet.crypto_performance.service;

import com.wallet.crypto_performance.infra.ScheduleConfig;
import com.wallet.crypto_performance.model.Asset;
import com.wallet.crypto_performance.util.PriceUpdateProperties;
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
    private final ScheduledExecutorService scheduler; // Scheduled thread executor for the periodic invocation of the price update
    private final ExecutorService threadPool; // Fixed thread pool for the price update request execution

    public PriceUpdateService(WalletService walletService, CoinService coinService, ScheduleConfig scheduleConfig, PriceUpdateProperties properties) {
        this.walletService = walletService;
        this.coinService = coinService;
        this.threadPool = Executors.newFixedThreadPool(properties.getThreads()); // Fixed thread pool to execute the update price requests
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateAssetPrices, 0, scheduleConfig.getPeriod(), TimeUnit.MILLISECONDS); // Schedule the task dynamically based on the period
    }

    public void updateAssetPrices() {
        List<Asset> assets = walletService.getAllAssets();
        for (Asset asset : assets) {
            threadPool.submit(() -> {
                log.info("Submitted request asset: {}", asset.getCoinId());
                var price = coinService.getCoinCurrentPrice(asset.getCoinId());
                price.ifPresent(curPrice -> {
                    log.info("New price for asset {}: {}", asset.getCoinId(), curPrice);
                    asset.setCurrentPrice(curPrice);
                    walletService.updateAsset(asset);
                });
            });
        }
    }

}
