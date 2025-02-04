package com.example.stockapi;

import java.util.List;

public interface StockNamesCallback {
    void onSuccess(List<String> stockNames);
    void onError(String errorMessage);
}
