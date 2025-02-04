package com.example.stocklib;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("stock")
    Call<Stock> getStock(@Query("name") String name);

    @GET("stocks")
    Call<List<String>> getAllStockNames();

    @GET("stockByDate")
    Call<StockPrice> getStockPriceByDate(
            @Query("name") String stockName,
            @Query("date") String date
    );

    @GET("stockByDateRange")
    Call<List<StockPrice>> getStockPricesByDateRange(
            @Query("name") String stockName,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("stockSummary")
    Call<Map<String, Object>> getStockSummary(
            @Query("name") String stockName,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("compareStocks")
    Call<List<Map<String, Object>>> compareStocks(
            @Query("names") List<String> stockNames,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );


}
