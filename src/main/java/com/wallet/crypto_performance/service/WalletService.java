package com.wallet.crypto_performance.service;

import com.wallet.crypto_performance.dto.AssetDTO;
import com.wallet.crypto_performance.exception.UnknownAssetPriceException;
import com.wallet.crypto_performance.exception.UnknownAssetException;
import com.wallet.crypto_performance.model.Asset;
import com.wallet.crypto_performance.model.AssetPerformance;
import com.wallet.crypto_performance.model.Wallet;
import com.wallet.crypto_performance.repository.AssetRepository;
import com.wallet.crypto_performance.repository.PerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private final AssetRepository assetRepository;
    private final PerformanceRepository performanceRepository;
    private final CoinService coinService;
    private final Wallet wallet;

    public WalletService(AssetRepository assetRepository, PerformanceRepository performanceRepository, CoinService coinService) {
        this.assetRepository = assetRepository;
        this.performanceRepository = performanceRepository;
        this.coinService = coinService;
        this.wallet = Wallet.getInstance();
    }

    public List<Asset> getAllAssets() {
        return wallet.getAssets();
    }

    public void updateAsset(Asset asset) {
        assetRepository.save(asset);
    }

    public List<Asset> addAssets(List<AssetDTO> assetDTOs) {
        List<Asset> assetEntities = assetDTOs.stream().map(this::convertToEntity).collect(Collectors.toList());
        List<Asset> savedAssets = assetRepository.saveAll(assetEntities);
        savedAssets.forEach(wallet::addAsset); // Add each asset to the in-memory wallet
        return savedAssets;
    }

    // Load all assets from the database into the in-memory wallet
    public void initializeWallet() {
        List<Asset> assets = assetRepository.findAll();
        wallet.getAssets().addAll(assets);
    }

    public void clearWallet() {
        wallet.clearAssets();
        assetRepository.deleteAll();
    }

    public AssetPerformance getWalletPerformance(LocalDate pastDate) {
        var containsDate = !Objects.isNull(pastDate);
        if (containsDate) {
            var pastPerformance = performanceRepository.findByDatePerformance(pastDate);
            if (pastPerformance.isPresent()) return pastPerformance.get();
        }
        var assets = containsDate ? assetsWithPastPrice(pastDate, wallet.getAssets()) : wallet.getAssets();
        var assetPerformance = calculateAssetsPerformance(assets);
        if (containsDate) {
            assetPerformance.setDatePerformance(pastDate);
            persistPerformance(assetPerformance);
        }
        return assetPerformance;
    }

    private void persistPerformance(AssetPerformance assetPerformance) {
        try {
            performanceRepository.save(assetPerformance);
        } catch (DataIntegrityViolationException e) {
            log.error("Performance for date {} could not be saved, already existent.", assetPerformance.getDatePerformance());
        }
    }

    private static AssetPerformance calculateAssetsPerformance(List<Asset> assets) {
        var asset = assets.get(0);
        var result = assetPerformance(asset);

        String bestAssetSymbol = asset.getSymbol(), worstAssetSymbol = asset.getSymbol();
        BigDecimal bestPerformance = result, worstPerformance = result, totalValue = assetAmount(asset);

        for (int i = 1; i < assets.size(); i++) {
            asset = assets.get(i);
            result = assetPerformance(asset);
            if (result.compareTo(bestPerformance) >= 0) {
                bestPerformance = result;
                bestAssetSymbol = asset.getSymbol();
            } else if (result.compareTo(worstPerformance) < 0) {
                worstPerformance = result;
                worstAssetSymbol = asset.getSymbol();
            }
            // Update total value from the wallet
            totalValue = totalValue.add(assetAmount(asset));
        }
        return new AssetPerformance(totalValue, bestAssetSymbol, bestPerformance, worstAssetSymbol, worstPerformance, LocalDate.now());
    }

    private List<Asset> assetsWithPastPrice(LocalDate pastDate, List<Asset> assets) {
        var returnAssets = Collections.synchronizedList(new ArrayList<Asset>(assets.size()));
        assets.parallelStream().forEach(asset -> {
            var previousPrice = coinService.getCoinPreviousPrice(asset.getCoinId(), pastDate).orElseThrow(() -> new UnknownAssetPriceException("The past asset price could not be retrieved %s".formatted(asset.getSymbol())));
            returnAssets.add(new Asset(asset.getId(), asset.getCoinId(), asset.getSymbol(), asset.getQuantity(), asset.getOriginalPrice(), previousPrice));
        });
        return returnAssets;
    }

    private static BigDecimal assetAmount(Asset asset) {
        return asset.getQuantity().multiply(asset.getCurrentPrice());
    }

    private static BigDecimal assetPerformance(Asset asset) {
        // Calculate the percentage change from the asset
        return asset.getCurrentPrice().divide(asset.getOriginalPrice(), RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100L));
    }

    private Asset convertToEntity(AssetDTO dto) {
        String coinId = coinService.getCoinIdBySymbol(dto.symbol()).orElseThrow(() -> new UnknownAssetException("The asset could not be recognized %s".formatted(dto.symbol())));
        BigDecimal currentPrice = coinService.getCoinCurrentPrice(coinId).orElseThrow(() -> new UnknownAssetPriceException("The asset price could not be retrieved %s".formatted(dto.symbol())));
        Asset asset = new Asset();
        asset.setSymbol(dto.symbol());
        asset.setCoinId(coinId);
        asset.setQuantity(dto.quantity());
        asset.setOriginalPrice(dto.originalPrice());
        asset.setCurrentPrice(currentPrice);
        return asset;
    }
}
