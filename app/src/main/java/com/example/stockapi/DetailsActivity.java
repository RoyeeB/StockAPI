package com.example.stockapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.stocklib.ApiServer;
import com.example.stocklib.ApiService;
import com.example.stocklib.Stock;
import com.example.stocklib.StockPrice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

   private Button back_BTN_details;
   private TextView symbol_TXT_DT , closePrice_TXT_DT,openePrice_TXT_DT,date_TXT_DT,highPrice_TXT_DT,lowPrice_TXT_DT,dailyPrecent_TXT_DT,dailyChange_TXT_DT,volume_TXT_DT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        String companyName = getIntent().getStringExtra("companyName");
        String selectedDate = getIntent().getStringExtra("selectedDate");

        initViews();
        clickedBack(companyName);
        fetchStockData(companyName , selectedDate);

    }
    public void initViews() {
        back_BTN_details = findViewById(R.id.back_BTN_details);
        symbol_TXT_DT = findViewById(R.id.symbol_TXT_DT);
        closePrice_TXT_DT = findViewById(R.id.closePrice_TXT_DT);
        openePrice_TXT_DT = findViewById(R.id.openePrice_TXT_DT);
        date_TXT_DT = findViewById(R.id.date_TXT_DT);
        highPrice_TXT_DT = findViewById(R.id.highPrice_TXT_DT);
        lowPrice_TXT_DT = findViewById(R.id.lowPrice_TXT_DT);
        dailyPrecent_TXT_DT = findViewById(R.id.dailyPrecent_TXT_DT);
        dailyChange_TXT_DT = findViewById(R.id.dailyChange_TXT_DT);
        volume_TXT_DT = findViewById(R.id.volume_TXT_DT);
    }

    public void clickedBack(String stockSymbol){
        back_BTN_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                Intent intent = new Intent(DetailsActivity.this, StockActivity.class);
                                intent.putExtra("stockSymbol", stockSymbol);
                                startActivity(intent);
                            }
        });

                        }
    // return stock data at the date
    private void fetchStockDataByDate(String stockName, String date) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);
        Call<StockPrice> call = apiService.getStockPriceByDate(stockName, date);

        call.enqueue(new Callback<StockPrice>() {
            @Override
            public void onResponse(Call<StockPrice> call, Response<StockPrice> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StockPrice stockPrice = response.body();

                    symbol_TXT_DT.setText(stockName);
                    lowPrice_TXT_DT.setText("Low price: $" + String.format("%.2f", stockPrice.getLowPrice()));
                    highPrice_TXT_DT.setText("High price: $" + String.format("%.2f", stockPrice.getHighPrice()));
                    date_TXT_DT.setText("Date: " + stockPrice.getDate());
                    closePrice_TXT_DT.setText("Close price: $" + String.format("%.2f", stockPrice.getClosePrice()));
                    openePrice_TXT_DT.setText("Open price: $" + String.format("%.2f", stockPrice.getOpenPrice()));
                    dailyPrecent_TXT_DT.setText("Daily precent change: " + String.format("%.2f" , stockPrice.getDailyChangePercent()) + "%");
                    dailyChange_TXT_DT.setText("Daily change: " + String.format("%.2f" , stockPrice.getDailyChange()));
                    volume_TXT_DT.setText("Volume: " + (long) stockPrice.getVolume());

                } else {
                    Log.e("StockError", "No data found for the given date!");
                }
            }

            @Override
            public void onFailure(Call<StockPrice> call, Throwable t) {
                Log.e("StockError", "Failed to fetch stock price: " + t.getMessage());
            }
        });
    }
// return stock data at the date
    private void fetchStockData(String stockSymbol , String date) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);
        Call<Stock> call = apiService.getStock(stockSymbol);
        Log.d("StockInfo", "Stock: " + stockSymbol);

        call.enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Stock stock = response.body();
                    double dateDouble = Double.parseDouble(date);
                    int dateInt = (int)Math.round(dateDouble);
                    fetchStockDataByDate(stockSymbol , stock.getPrices().get(dateInt).getDate());
                } else
                    Log.e("StockInfo", "Failed to fetch stock data: " + response.errorBody());
            }

            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                Log.e("StockInfo", "Error: " + t.getMessage());
            }
        });
    }
}