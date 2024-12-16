package com.wallet.crypto_performance.controller;

import com.wallet.crypto_performance.dto.AssetDTO;
import com.wallet.crypto_performance.dto.AssetsPerformanceDTO;
import com.wallet.crypto_performance.exception.InvalidPastDateParamException;
import com.wallet.crypto_performance.model.Asset;
import com.wallet.crypto_performance.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final WalletService walletService;

    public AssetController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(summary = "Get all assets", description = "Retrieve a list of all assets in the wallet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public List<AssetDTO> getAllAssets() {
        return walletService.getAllAssets().stream().map(Asset::toDto).collect(Collectors.toList());
    }

    @Operation(summary = "Get performance of the assets", description = """
            A JSON with the total value of the wallet and the best and worst assets.
            Past performances can be requested for a specific past day, with the format yyyy-MM-dd,
            using the query param "pastDateParam\"""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "4XX", description = "Bad arguments"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/performance")
    public AssetsPerformanceDTO getAssetsPerformance(@RequestParam(required = false) String pastDateParam) throws InvalidPastDateParamException {
        var pastDate = parseISODate(pastDateParam);
        return walletService.getWalletPerformance(pastDate).toDto();
    }

    @Operation(summary = "Add an asset to the wallet", description = "Add a list of assets with the fields: symbol, quantity, price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "4XX", description = "Bad arguments"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public List<AssetDTO> addAssets(@RequestBody List<AssetDTO> assetDTOs) {
        return walletService.addAssets(assetDTOs).stream().map(Asset::toDto).collect(Collectors.toList());
    }

    @Operation(summary = "Clear entire wallet", description = "Remove all assets from the wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "4XX", description = "Bad arguments"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping
    public ResponseEntity<Void> clearWallet() {
        walletService.clearWallet();
        return ResponseEntity.noContent().build();
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
