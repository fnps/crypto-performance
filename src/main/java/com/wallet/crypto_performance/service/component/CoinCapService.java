package com.wallet.crypto_performance.service.component;

import com.wallet.crypto_performance.dto.AssetApiResponseDTO;
import com.wallet.crypto_performance.dto.AssetByIdApiResponseDTO;
import com.wallet.crypto_performance.dto.AssetsApiResponseDTO;
import com.wallet.crypto_performance.service.CoinService;
import com.wallet.crypto_performance.util.PriceUpdateProperties;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.Optional;

@Component
public class CoinCapService implements CoinService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CoinCapService.class);
    RestTemplate restTemplate;
    PriceUpdateProperties properties;

    public CoinCapService(RestTemplate restTemplate, PriceUpdateProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    // TODO verify successful response before get data value

    @Override
    public Optional<String> getCoinIdBySymbol(String symbol) {
        log.debug("Requesting asset api by symbol: {}, api: {}", symbol, properties.getAssetsApi());
        var response = restTemplate.getForEntity(properties.getAssetsApi(), AssetsApiResponseDTO.class, symbol);
        if (response.getStatusCode().isError()) {
            log.error("Error requesting asset details, http status code: {}", response.getStatusCode().value());
            return Optional.empty();
        }
        log.debug("Success request asset api by symbol: {}, api: {}", symbol, properties.getAssetsApi());
        var data = Objects.requireNonNull(response.getBody(), "Null coin service api response").data();
        return data.stream().filter(e -> e.symbol().equalsIgnoreCase(symbol)).map(AssetApiResponseDTO::id).findFirst();
    }

    @Override
    public Optional<BigDecimal> getCoinCurrentPrice(String coinId) {
        log.debug("Requesting asset detail by id: {}, api: {}", coinId, properties.getAssetByIdApi());
        var response = restTemplate.getForEntity(properties.getAssetByIdApi(), AssetByIdApiResponseDTO.class, coinId);
        if (response.getStatusCode().isError()) {
            log.error("Error requesting asset detail by id, http status code: {}", response.getStatusCode().value());
            return Optional.empty();
        }
        log.debug("Success request asset detail by id: {}, api: {}", coinId, properties.getAssetByIdApi());
        var data = Objects.requireNonNull(response.getBody(), "Null coin service api response").data();
        return Optional.of(new BigDecimal(data.priceUsd()));
    }

    @Override
    public Optional<BigDecimal> getCoinPreviousPrice(String coinId, LocalDate pastDate) {
        log.debug("Requesting asset history by id: {}, date: {}, api: {}", coinId, pastDate, properties.getAssetByIdHistoryApi());
        var instant = pastDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        var response = restTemplate.getForEntity(properties.getAssetByIdHistoryApi(), AssetsApiResponseDTO.class, coinId, instant.toEpochMilli(), instant.plus(Duration.ofDays(1L)).toEpochMilli());
        if (response.getStatusCode().isError()) {
            log.error("Error requesting asset history by id, http status code: {}", response.getStatusCode().value());
            return Optional.empty();
        }
        log.debug("Success request asset history by id: {}, date: {}, api: {}", coinId, pastDate, properties.getAssetByIdHistoryApi());
        var data = Objects.requireNonNull(response.getBody(), "Null coin service api response").data();
        return Optional.of(new BigDecimal(data.get(0).priceUsd()));
    }
}
