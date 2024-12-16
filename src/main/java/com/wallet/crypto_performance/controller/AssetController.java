package com.wallet.crypto_performance.controller;

import com.wallet.crypto_performance.dto.AssetDTO;
import com.wallet.crypto_performance.dto.AssetsPerformanceDTO;
import com.wallet.crypto_performance.exception.InvalidPastDateParamException;
import com.wallet.crypto_performance.model.Asset;
import com.wallet.crypto_performance.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
        return walletService.getAllAssets().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/performance")
    public AssetsPerformanceDTO getAssetsPerformance(@RequestParam(required = false) String pastDateParam) throws InvalidPastDateParamException {
        var pastDate = parseISODate(pastDateParam);
        return walletService.getWalletPerformance(pastDate);
    }

    @PostMapping
    public List<AssetDTO> addAssets(@RequestBody List<AssetDTO> assetDTOs) {
        return walletService.addAssets(assetDTOs).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @DeleteMapping
    public ResponseEntity<Void> clearWallet() {
        walletService.clearWallet();
        return ResponseEntity.noContent().build();
    }

    private AssetDTO convertToDTO(Asset asset) {
        return new AssetDTO(asset.getSymbol(), asset.getQuantity(), asset.getOriginalPrice());
    }

    private static LocalDate parseISODate(String date) throws InvalidPastDateParamException {
        if (date == null) {
            return null;
        }

        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new InvalidPastDateParamException("Please inform a past date param with the format: yyyy-MM-dd");
        }
    }

}
