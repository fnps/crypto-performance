package com.wallet.crypto_performance.service;

import com.wallet.crypto_performance.dto.AssetDTO;
import com.wallet.crypto_performance.exception.EmptyWalletException;
import com.wallet.crypto_performance.model.Asset;
import com.wallet.crypto_performance.model.AssetPerformance;
import com.wallet.crypto_performance.model.Wallet;
import com.wallet.crypto_performance.repository.AssetRepository;
import com.wallet.crypto_performance.repository.PerformanceRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WalletServiceTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private PerformanceRepository performanceRepository;
    @Mock
    private CoinService coinService;

    @Autowired
    @InjectMocks
    private WalletService walletService;

    @BeforeAll
    static void setWallet() {
        List<Asset> assets = new ArrayList<>();
        assets.add(new Asset(1L, "bitcoin", "BTC", BigDecimal.valueOf(2.5), BigDecimal.valueOf(45000.00), BigDecimal.valueOf(105954.2735257874865383))); // Current price can be updated later
        assets.add(new Asset(2L, "ethereum", "ETH", BigDecimal.valueOf(10), BigDecimal.valueOf(3000.0), BigDecimal.valueOf(4036.2031302579319626)));
        assets.add(new Asset(3L, "litecoin", "LTC", BigDecimal.valueOf(10), BigDecimal.valueOf(300.00), BigDecimal.valueOf(119.7807914656272345)));
        Wallet.getInstance().getAssets().clear();
        Wallet.getInstance().getAssets().addAll(assets);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Other service methods have trivial logic that is not necessary to be tested individually

    @Test
    @Order(1)
    void calculatePerformanceCurrentDayNotQueryingPerformance() {
        when(performanceRepository.findByDatePerformance(any())).thenReturn(Optional.empty());
        AssetPerformance expected = new AssetPerformance(BigDecimal.valueOf(306445.52), "BTC", BigDecimal.valueOf(135.45), "LTC", BigDecimal.valueOf(-60.07), LocalDate.now());
        var actual = walletService.getWalletPerformance(null);
        assertEquals(expected.getTotal(), actual.getTotal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getBestPerformance(), actual.getBestPerformance().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getWorstPerformance(), actual.getWorstPerformance().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getBestAsset(), actual.getBestAsset());
        assertEquals(expected.getWorstAsset(), actual.getWorstAsset());
        verify(performanceRepository, times(0)).findByDatePerformance(any());
        verify(coinService, times(0)).getCoinPreviousPrice(any(), any());
    }

    @Test
    @Order(2)
    void calculatePerformancePastDayQueryingPerformance() {
        AssetPerformance expected = new AssetPerformance(BigDecimal.valueOf(306445.52), "BTC", BigDecimal.valueOf(135.45), "LTC", BigDecimal.valueOf(-60.07), LocalDate.now());
        when(performanceRepository.findByDatePerformance(any())).thenReturn(Optional.of(expected));
        var actual = walletService.getWalletPerformance(LocalDate.of(2024, 12, 12));
        assertEquals(expected.getTotal(), actual.getTotal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getBestPerformance(), actual.getBestPerformance().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getWorstPerformance(), actual.getWorstPerformance().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getBestAsset(), actual.getBestAsset());
        assertEquals(expected.getWorstAsset(), actual.getWorstAsset());
        verify(performanceRepository, times(1)).findByDatePerformance(any());
        verify(coinService, times(0)).getCoinPreviousPrice(any(), any());
    }

    @Test
    @Order(3)
    void calculatePerformancePastDayQueryingMissingPerformance() {
        AssetPerformance expected = new AssetPerformance(BigDecimal.valueOf(306445.52), "BTC", BigDecimal.valueOf(135.45), "LTC", BigDecimal.valueOf(-60.07), LocalDate.now());
        when(performanceRepository.findByDatePerformance(any())).thenReturn(Optional.empty());
        when(coinService.getCoinPreviousPrice(eq("bitcoin"), any())).thenReturn(Optional.of(BigDecimal.valueOf(105954.2735257874865383)));
        when(coinService.getCoinPreviousPrice(eq("ethereum"), any())).thenReturn(Optional.of(BigDecimal.valueOf(4036.2031302579319626)));
        when(coinService.getCoinPreviousPrice(eq("litecoin"), any())).thenReturn(Optional.of(BigDecimal.valueOf(119.7807914656272345)));
        var actual = walletService.getWalletPerformance(LocalDate.of(2024, 12, 12));
        assertEquals(expected.getTotal(), actual.getTotal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getBestPerformance(), actual.getBestPerformance().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getWorstPerformance(), actual.getWorstPerformance().setScale(2, RoundingMode.HALF_UP));
        assertEquals(expected.getBestAsset(), actual.getBestAsset());
        assertEquals(expected.getWorstAsset(), actual.getWorstAsset());
        assertEquals(LocalDate.of(2024, 12, 12), actual.getDatePerformance());
        verify(performanceRepository, times(1)).findByDatePerformance(any());
        verify(coinService, times(3)).getCoinPreviousPrice(any(), any());
    }

    @Test
    @Order(990)
    void calculatePerformanceOnEmptyWalletReturnError() {
        Wallet.getInstance().getAssets().clear();
        assertThrows(EmptyWalletException.class, () -> walletService.getWalletPerformance(LocalDate.now()));
    }

    @Test
    @Order(999)
    void getCorrectCoinIdBySymbol() {
        when(assetRepository.saveAll(any())).thenReturn(List.of(new Asset(4L, "dogecoin", "doge", BigDecimal.TEN, BigDecimal.valueOf(0.555555), BigDecimal.valueOf(105954156.2735257874865383))));
        when(coinService.getCoinIdBySymbol(any())).thenReturn(Optional.of("dogecoin"));
        when(coinService.getCoinCurrentPrice(any())).thenReturn(Optional.of(BigDecimal.valueOf(105954156.2735257874865383)));
        var actual = walletService.addAssets(List.of(new AssetDTO("doge", BigDecimal.TEN, BigDecimal.valueOf(0.555555))));
        assertEquals("dogecoin", actual.get(0).getCoinId());
        assertEquals(BigDecimal.TEN, actual.get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(0.555555), actual.get(0).getOriginalPrice());
        verify(assetRepository, times(1)).saveAll(any());
        verify(coinService, times(1)).getCoinIdBySymbol(any());
        verify(coinService, times(1)).getCoinCurrentPrice(any());
    }
}