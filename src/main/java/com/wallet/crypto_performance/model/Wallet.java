package com.wallet.crypto_performance.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    public void clearAssets() {
        assets.clear();
    }

    public List<Asset> getAssets() {
        return assets;
    }
}
