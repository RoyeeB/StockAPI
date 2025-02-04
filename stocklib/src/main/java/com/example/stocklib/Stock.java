package com.example.stocklib;

import java.util.List;

public class Stock {
    private String companyName;
    private String name;
    private String logo;
    private List<StockPrice> prices;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    public List<StockPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<StockPrice> prices) {
        this.prices = prices;
    }


    public Double getLastClosingPrice() {
        if (prices != null && !prices.isEmpty()) {
            return (double) prices.get(prices.size() - 1).getClosePrice();
        }

        return null;
    }
}
