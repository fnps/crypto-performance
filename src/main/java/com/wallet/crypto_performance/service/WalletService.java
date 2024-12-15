package com.wallet.crypto_performance.service;

import com.wallet.crypto_performance.model.Asset;
import com.wallet.crypto_performance.model.Wallet;
import com.wallet.crypto_performance.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    private final AssetRepository assetRepository;
    private final Wallet wallet;

    public WalletService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
        this.wallet = Wallet.getInstance();
    }

    public List<Asset> getAllAssets() {
        return wallet.getAssets();
    }

    public List<Asset> addAssets(List<Asset> assets) {
        List<Asset> savedAssets = assetRepository.saveAll(assets);
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
}
