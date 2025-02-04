package com.example.stockapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.stocklib.ApiServer;
import com.example.stocklib.ApiService;
import com.example.stocklib.Stock;
import com.example.stocklib.StockPrice;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class MainActivity extends AppCompatActivity {
    private RecyclerView stocksRecyclerView; // רכיב ה-RecyclerView
    private StocksAdapter adapter;

    private EditText stock_EDT_symbol;
    private Button search_BTN_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         initViews ();
         clickedSearch();

        stocksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new StocksAdapter(new ArrayList<>());
        stocksRecyclerView.setAdapter(adapter);
        fetchAndDisplayStocks();

    }


    public void initViews (){
        stock_EDT_symbol = findViewById(R.id.stock_EDT_symbol);
        search_BTN_main = findViewById(R.id.search_BTN_main);
        stocksRecyclerView = findViewById(R.id.stocksRecyclerView);
    }
//search function
    private void clickedSearch() {
        search_BTN_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stockSymbol = stock_EDT_symbol.getText().toString().trim().toUpperCase();
                if (stockSymbol.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Empty symbol!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                    checkStockAvailable(stockSymbol, new StockAvailabilityCallback() {
                        @Override
                        public void onResult(boolean isAvailable) {
                            if (isAvailable) {
                                Intent intent = new Intent(MainActivity.this, StockActivity.class);
                                intent.putExtra("stockSymbol", stockSymbol);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Error symbol!", Toast.LENGTH_SHORT).show();
                            }
                            }

                    });
            }
        });
    }
// Checks if the stock is in the database
    public void checkStockAvailable(String stockSymbol, StockAvailabilityCallback callback) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);
        Call<Stock> call = apiService.getStock(stockSymbol);

        call.enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                callback.onResult(false);
                Log.e("StockInfo", "Error checking stock: " + t.getMessage());
            }
        });
    }


//return the names of stocks
    public void fetchStockNames(StockNamesCallback callback) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);

        Call<List<String>> call = apiService.getAllStockNames();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Response failed: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                callback.onError("API call failed: " + t.getMessage());
            }
        });
    }
//return stock data
    private void fetchStockData(String stockSymbol, StockDetailsCallback callback) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);
        Call<Stock> call = apiService.getStock(stockSymbol);
        Log.d("StockInfo", "Fetching data for stock: " + stockSymbol);

        call.enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Stock stock = response.body();
                    callback.onSuccess(stock);
                } else {
                    callback.onError("Failed to fetch stock data: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                callback.onError("API call failed: " + t.getMessage());
            }
        });
    }
// for the view cards
    public void fetchAndDisplayStocks() {
        if (adapter == null) {
            adapter = new StocksAdapter(new ArrayList<>());
            stocksRecyclerView.setAdapter(adapter);
        }

        fetchStockNames(new StockNamesCallback() {
            @Override
            public void onSuccess(List<String> stockSymbols) {
                for (String symbol : stockSymbols) {
                    fetchStockData(symbol, new StockDetailsCallback() {
                        @Override
                        public void onSuccess(Stock stock) {
                            runOnUiThread(() -> adapter.addStock(stock)); // ודא שה-Adapter לא `null`
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("StockAPI", "Error fetching stock details: " + errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("StockAPI", "Error fetching stock symbols: " + errorMessage);
            }
        });
    }

    // return stock details at the dates
    private void fetchStockPricesByDateRange(String stockName, String startDate, String endDate) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);

        Call<List<StockPrice>> call = apiService.getStockPricesByDateRange(stockName, startDate, endDate);
        call.enqueue(new Callback<List<StockPrice>>() {
            @Override
            public void onResponse(Call<List<StockPrice>> call, Response<List<StockPrice>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<StockPrice> stockPrices = response.body();

                    for (StockPrice stockPrice : stockPrices) {
                        Log.d("StockInfo", "Date: " + stockPrice.getDate());
                        Log.d("StockInfo", "Close Price: $" + String.format("%.2f", stockPrice.getClosePrice()));
                        Log.d("StockInfo", "Open Price: $" + String.format("%.2f", stockPrice.getOpenPrice()));
                    }

                } else {
                    Log.e("StockError", "No data found for the given date range!");
                }
            }

            @Override
            public void onFailure(Call<List<StockPrice>> call, Throwable t) {
                Log.e("StockError", "Failed to fetch stock prices: " + t.getMessage());
            }
        });
    }

    // compare stocks
    private void fetchComparison(List<String> stockNames, String startDate, String endDate) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);

        Call<List<Map<String, Object>>> call = apiService.compareStocks(stockNames, startDate, endDate);
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> comparison = response.body();
                    Log.d("API Request", "URL: " + call.request().url());

                    // הדפסת הנתונים בלוג
                    for (Map<String, Object> stockData : comparison) {
                        Log.d("StockComparison", "Name: " + stockData.get("name"));
                        Log.d("StockComparison", "Average Close Price: $" + stockData.get("averageClosePrice"));
                        Log.d("StockComparison", "Average Open Price: $" + stockData.get("averageOpenPrice"));
                        Log.d("StockComparison", "Max Price: $" + stockData.get("maxPrice"));
                        Log.d("StockComparison", "Min Price: $" + stockData.get("minPrice"));
                        Log.d("StockComparison", "Total Volume: " + stockData.get("totalVolume"));
                        Log.d("StockComparison", "Percentage Change: " + stockData.get("percentageChange") + "%");
                    }
                } else {
                    Log.e("StockComparison", "Failed to fetch stock comparison: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("StockComparison", "Error: " + t.getMessage());
            }
        });
    }


    }


