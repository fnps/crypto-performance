package com.wallet.crypto_performance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Wallet {

    private static Wallet instance;
    private final List<Asset> assets = new ArrayList<>();

    private Wallet() {}

    public static Wallet getInstance() {
        if (instance == null) {
            instance = new Wallet();
        }
        return instance;
    }

    public void addAsset(Asset asset) {
        this.assets.add(asset);
    }

    public void removeAsset(Long assetId) {
        assets.removeIf(asset -> asset.getId().equals(assetId));
    }

    public void clearAssets() {
        assets.clear();
    }
}
