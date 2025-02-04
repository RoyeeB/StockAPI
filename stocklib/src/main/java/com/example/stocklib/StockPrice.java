package com.example.stocklib;

public class StockPrice {

    private String date;
    private double closePrice;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double dailyChange;
    private Double dailyChangePercent;
    private Long volume;

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getClosePrice() {
        return closePrice;
    }


    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Double getDailyChange() {
        return dailyChange;
    }

    public void setDailyChange(Double dailyChange) {
        this.dailyChange = dailyChange;
    }

    public Double getDailyChangePercent() {
        return dailyChangePercent;
    }

    public void setDailyChangePercent(Double dailyChangePercent) {
        this.dailyChangePercent = dailyChangePercent;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }


}
