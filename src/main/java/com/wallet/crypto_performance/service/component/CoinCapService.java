package com.wallet.crypto_performance.service.component;

import com.wallet.crypto_performance.dto.AssetApiResponseDTO;
import com.wallet.crypto_performance.dto.AssetByIdApiResponseDTO;
import com.wallet.crypto_performance.dto.AssetsApiResponseDTO;
import com.wallet.crypto_performance.service.CoinService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class CoinCapService implements CoinService {

    RestTemplate restTemplate;

    public CoinCapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // TODO change hardcoded uris to property
    // TODO verify successful response before get data value

    @Override
    public String getCoinIdBySymbol(String symbol) {
        var response =  restTemplate.getForEntity("https://api.coincap.io/v2/assets", AssetsApiResponseDTO.class);
        return response.getBody().data().stream().filter(e -> e.symbol().equalsIgnoreCase(symbol)).map(AssetApiResponseDTO::id).findFirst().orElseThrow();
    }

    @Override
    public BigDecimal getCoinCurrentPrice(String coinId) {
        // TODO change hardcoded uri to property
        var response =  restTemplate.getForEntity("https://api.coincap.io/v2/assets/" + coinId, AssetByIdApiResponseDTO.class);
        return new BigDecimal(response.getBody().data().priceUsd());
    }

    @Override
    public BigDecimal getCoinPreviousPrice(Instant pastDate) {
        return null;
    }
}
