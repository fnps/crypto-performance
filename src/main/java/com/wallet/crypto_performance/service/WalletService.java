package com.wallet.crypto_performance.service;

import com.wallet.crypto_performance.dto.AssetDTO;
import com.wallet.crypto_performance.dto.AssetsPerformanceDTO;
import com.wallet.crypto_performance.model.Asset;
import com.wallet.crypto_performance.model.Wallet;
import com.wallet.crypto_performance.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private final AssetRepository assetRepository;
    private final CoinService coinService;
    private final Wallet wallet;

    public WalletService(AssetRepository assetRepository, CoinService coinService) {
        this.assetRepository = assetRepository;
        this.coinService = coinService;
        this.wallet = Wallet.getInstance();
    }

    public List<Asset> getAllAssets() {
        return wallet.getAssets();
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

    public AssetsPerformanceDTO getWalletPerformance() {
        var assets = wallet.getAssets();
        var asset = assets.get(0);
        // Calculate the percentage change from the asset
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
        return new AssetsPerformanceDTO(totalValue, bestAssetSymbol, bestPerformance, worstAssetSymbol, worstPerformance);
    }

    private static BigDecimal assetAmount(Asset asset) {
        return asset.getQuantity().multiply(asset.getCurrentPrice());
    }

    private static BigDecimal assetPerformance(Asset asset) {
        return asset.getCurrentPrice().divide(asset.getOriginalPrice(), RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100L));
    }

    private Asset convertToEntity(AssetDTO dto) {
        String coinId = coinService.getCoinIdBySymbol(dto.symbol());
        BigDecimal currentPrice = coinService.getCoinCurrentPrice(coinId);
        Asset asset = new Asset();
        asset.setSymbol(dto.symbol());
        asset.setCoinId(coinId);
        asset.setQuantity(dto.quantity());
        asset.setOriginalPrice(dto.originalPrice());
        asset.setCurrentPrice(currentPrice); // Set the same to avoid nullable exception at database insert
        return asset;
    }
}
