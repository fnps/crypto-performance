package com.wallet.crypto_performance.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "price.update.api")
public class PriceUpdateProperties {
    private Integer threads;
    private String assetsApi;
    private String assetByIdApi;
    private String assetByIdHistoryApi;

    public String getAssetsApi() {
        return assetsApi;
    }

    public void setAssetsApi(String assetsApi) {
        this.assetsApi = assetsApi;
    }

    public String getAssetByIdApi() {
        return assetByIdApi;
    }

    public void setAssetByIdApi(String assetByIdApi) {
        this.assetByIdApi = assetByIdApi;
    }

    public String getAssetByIdHistoryApi() {
        return assetByIdHistoryApi;
    }

    public void setAssetByIdHistoryApi(String assetByIdHistoryApi) {
        this.assetByIdHistoryApi = assetByIdHistoryApi;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }


}
