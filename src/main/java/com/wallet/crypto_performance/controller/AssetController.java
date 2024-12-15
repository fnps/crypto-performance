package com.wallet.crypto_performance.controller;

import com.wallet.crypto_performance.dto.AssetDTO;
import com.wallet.crypto_performance.model.Asset;
import com.wallet.crypto_performance.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final WalletService walletService;

    public AssetController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public List<AssetDTO> getAllAssets() {
        return walletService.getAllAssets()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public List<AssetDTO> addAssets(@RequestBody List<AssetDTO> assetDTOs) {
        List<Asset> assets = assetDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        return walletService.addAssets(assets)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping
    public ResponseEntity<Void> clearWallet() {
        walletService.clearWallet();
        return ResponseEntity.noContent().build();
    }

    private AssetDTO convertToDTO(Asset asset) {
        return new AssetDTO(asset.getSymbol(), asset.getQuantity(), asset.getOriginalPrice());
    }

    private Asset convertToEntity(AssetDTO dto) {
        Asset asset = new Asset();
        asset.setSymbol(dto.symbol());
        asset.setQuantity(dto.quantity());
        asset.setOriginalPrice(dto.originalPrice());
        asset.setCurrentPrice(dto.originalPrice()); // Set the same to avoid nullable exception at database insert
        return asset;
    }
}
