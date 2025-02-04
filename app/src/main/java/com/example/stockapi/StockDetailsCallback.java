package com.example.stockapi;

import com.example.stocklib.Stock;

public interface StockDetailsCallback {
    void onSuccess(Stock stock);
    void onError(String errorMessage);
}

