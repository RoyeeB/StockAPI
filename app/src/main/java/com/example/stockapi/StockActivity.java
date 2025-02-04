package com.example.stockapi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.stocklib.ApiServer;
import com.example.stocklib.ApiService;
import com.example.stocklib.Stock;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockActivity extends AppCompatActivity {
    private TextView companyName_TXT, stockSymbol_TXT, precentChange_TXT, lastClosePrice_TXT,summeryMinPrice_TXT,summeryMaxPrice_TXT,summeryPrecent_TXT,summeryClose_TXT;
    private ImageView logo_ic;
    private Button back_BTN_stock ,   biMonthlyButton,monthlyButton,weeklyButton,dailyButton ;
    private LineChart stockChart;
    private LineDataSet dataSet;
    private Map<String, Integer> datePositionMap = new HashMap<>();
    private int xPosition = 0;
    private float previousPrice = 0f;
    private List<Entry> livePriceData = new ArrayList<>();
    private int currentDisplayedDays = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock);
        initViews();
        initializeChart();
        clickedBack();
        String stockSymbol = getIntent().getStringExtra("stockSymbol");
       fetchStockData(stockSymbol);

        dailyButton.setOnClickListener(view -> {
            currentDisplayedDays = 1;
            fetchStockData1(stockSymbol,1);
            unVisibleTXT();
        });

        weeklyButton.setOnClickListener(view -> {
            currentDisplayedDays = 7;
            fetchStockData1(stockSymbol,7);
            fetchStockSummary(stockSymbol,7);
            visibleTXT();
        });

        monthlyButton.setOnClickListener(view -> {
            currentDisplayedDays = 30;
            fetchStockData1(stockSymbol,30);
            fetchStockSummary(stockSymbol,30);
            visibleTXT();
        });

        biMonthlyButton.setOnClickListener(view -> {
            currentDisplayedDays = 60;
            fetchStockData1(stockSymbol,60);
            fetchStockSummary(stockSymbol,60);
            visibleTXT();
        });

        handler.postDelayed(updateTask, 2000);

        stockChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float dateIndex = e.getX();
                String companyName = stockSymbol;
                String selectedDate = String.valueOf(dateIndex) ;

                Intent intent = new Intent(StockActivity.this, DetailsActivity.class);
                intent.putExtra("companyName", companyName);
                intent.putExtra("selectedDate", selectedDate);
                startActivity(intent);
            }
            @Override
            public void onNothingSelected() {
            }
        });
    }

    public void visibleTXT (){
        summeryMinPrice_TXT.setVisibility(View.VISIBLE);
        summeryMaxPrice_TXT.setVisibility(View.VISIBLE);
        summeryPrecent_TXT.setVisibility(View.VISIBLE);
        summeryClose_TXT.setVisibility(View.VISIBLE);
    }

    public void unVisibleTXT (){
        summeryMinPrice_TXT.setVisibility(View.GONE);
        summeryMaxPrice_TXT.setVisibility(View.GONE);
        summeryPrecent_TXT.setVisibility(View.GONE);
        summeryClose_TXT.setVisibility(View.GONE);
    }

    public void initViews() {
        companyName_TXT = findViewById(R.id.companyName_TXT);
        stockSymbol_TXT = findViewById(R.id.stockSymbol_TXT);
        lastClosePrice_TXT = findViewById(R.id.lastClosePrice_TXT);
        precentChange_TXT = findViewById(R.id.precentChange_TXT);
        logo_ic = findViewById(R.id.logo_ic);
        stockChart = findViewById(R.id.stock_chart);
        back_BTN_stock = findViewById(R.id.back_BTN_stock);
         dailyButton = findViewById(R.id.dailyButton);
         weeklyButton = findViewById(R.id.weeklyButton);
         monthlyButton = findViewById(R.id.monthlyButton);
         biMonthlyButton = findViewById(R.id.biMonthlyButton);
        summeryMinPrice_TXT = findViewById(R.id.summeryMinPrice_TXT);
        summeryMaxPrice_TXT = findViewById(R.id.summeryMaxPrice_TXT);
        summeryPrecent_TXT = findViewById(R.id.summeryPrecent_TXT);
        summeryClose_TXT = findViewById(R.id.summeryClose_TXT);
        unVisibleTXT();
    }

    public void clickedBack(){
        back_BTN_stock.setOnClickListener(v -> {
            startActivity(new Intent(StockActivity.this, MainActivity.class));
        });
    }
// return stock summary
    private void fetchStockSummary(String stockSymbol, int days) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);
        Call<Stock> call = apiService.getStock(stockSymbol);

        call.enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Stock stock = response.body();
                    int startDate = stock.getPrices().size()-days;
                    int endDate = stock.getPrices().size()-1;
                    showSummary(stock.getCompanyName(),stock.getPrices().get(startDate).getDate(),stock.getPrices().get(endDate).getDate());
                } else
                    Log.e("StockInfo", "Failed to fetch stock data: " + response.errorBody());
            }
            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                Log.e("StockInfo", "Error: " + t.getMessage());
            }
        });
    }

    public void showSummary(String stockName , String startDate , String endDate) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getStockSummary(stockName, startDate, endDate);
        Log.d("StockInfo", "Stock: " + stockName + "Start Date:" + startDate + "End Date:" + endDate);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> stockSummary = response.body();

                    summeryMinPrice_TXT.setText("Min Price: " + stockSummary.get("minPrice"));
                    summeryMaxPrice_TXT.setText("Max Price: " + stockSummary.get("maxPrice"));
                    summeryPrecent_TXT.setText("Change: " + stockSummary.get("percentageChange") + "%");
                    summeryClose_TXT.setText("Avg Close Price: " + stockSummary.get("averageClosePrice"));
                } else {
                    Log.e("StockSummary", "Response failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("StockSummary", "Request failed", t);
            }
        });
    }


// return stock data by symbol
    private void fetchStockData(String stockSymbol) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);
        Call<Stock> call = apiService.getStock(stockSymbol);
        Log.d("StockInfo", "Stock: " + stockSymbol);
        call.enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Stock stock = response.body();

                    for (int i = 0; i < stock.getPrices().size(); i++) {
                        addStockData(stock.getPrices().get(i).getDate() ,stock.getPrices().get(i).getClosePrice() );
                    }



                    companyName_TXT.setText(stock.getCompanyName());
                    stockSymbol_TXT.setText(stock.getName());
                    DecimalFormat df = new DecimalFormat("#.##");
                    lastClosePrice_TXT.setText(df.format(stock.getLastClosingPrice()));
                    Glide.with(StockActivity.this)
                            .load(stock.getLogo())
                            .into(logo_ic);
                    precentChange_TXT.setText(df.format(stock.getPrices().get(59).getDailyChangePercent()) + "%");
                    if (stock.getPrices().get(59).getDailyChangePercent() < 0)
                        precentChange_TXT.setTextColor(Color.RED);
                    else
                        precentChange_TXT.setTextColor(Color.GREEN);

                } else
                    Log.e("StockInfo", "Failed to fetch stock data: " + response.errorBody());

            }


            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                Log.e("StockInfo", "Error: " + t.getMessage());
            }
        });
    }

    private void initializeChart() {
        stockChart.setDrawGridBackground(false);
        stockChart.setDrawBorders(true);
        stockChart.setBorderColor(Color.LTGRAY);
        stockChart.setTouchEnabled(true);
        stockChart.setDragEnabled(true);
        stockChart.setScaleEnabled(true);

        XAxis xAxis = stockChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                for (Map.Entry<String, Integer> entry : datePositionMap.entrySet()) {
                    if (entry.getValue() == (int)value) {
                        return entry.getKey();
                    }
                }
                return "";
            }
        });

        YAxis leftAxis = stockChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        stockChart.getAxisRight().setEnabled(false);
        stockChart.getLegend().setEnabled(true);
    }

    public void addStockData(String date, double price) {
        if (!datePositionMap.containsKey(date)) {
            datePositionMap.put(date, xPosition++);
        }
        int position = datePositionMap.get(date);

        if (dataSet == null) {
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(position, (float)price));
            dataSet = new LineDataSet(entries, "Stock price");
            styleDataSet(dataSet);
            LineData lineData = new LineData(dataSet);
            stockChart.setData(lineData);
        } else {
            List<Entry> currentEntries = new ArrayList<>(dataSet.getValues());
            currentEntries.add(new Entry(position, (float)price));
            Collections.sort(currentEntries, (e1, e2) ->
                    Float.compare(e1.getX(), e2.getX()));
            dataSet.setValues(currentEntries);
            stockChart.getData().notifyDataChanged();
        }
        stockChart.notifyDataSetChanged();
        stockChart.invalidate();
    }


    private void styleDataSet(LineDataSet dataSet) {
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setCircleRadius(2f);
        dataSet.setDrawValues(false);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.2f", value);
            }
        });
    }


class StockDataPoint {
    private String date;
    private double price;

    public StockDataPoint(String date, double price) {
        this.date = date;
        this.price = price;
    }

    public String getDate() { return date; }
    public double getPrice() { return price; }
}

    private final Handler handler = new Handler();
    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            shiftLastEntryRandomly();
            handler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(updateTask, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTask);
    }


    private void fetchStockData1(String stockSymbol, int days) {
        ApiService apiService = ApiServer.getClient().create(ApiService.class);
        Call<Stock> call = apiService.getStock(stockSymbol);
        Log.d("StockInfo", "Fetching stock: " + stockSymbol);

        call.enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("StockInfo", "Failed to fetch stock data: " + response.errorBody());
                    return;
                }
                Stock stock = response.body();
                if (stock == null || stock.getPrices() == null) {
                    return;
                }

                int dataSize = stock.getPrices().size();
                if (dataSize < days) {
                    return;
                }
                if (stockChart.getData() != null) {
                    stockChart.clear();
                }
                stockChart.setData(new LineData());
                datePositionMap.clear();
                xPosition = 0;

                dataSet = new LineDataSet(new ArrayList<>(), "Stock Price");
                styleDataSet(dataSet);
                stockChart.getData().addDataSet(dataSet);

                for (int i = dataSize - days; i < dataSize; i++) {
                    String date = stock.getPrices().get(i).getDate();
                    double price = stock.getPrices().get(i).getClosePrice();
                    if (!datePositionMap.containsKey(date)) {
                        datePositionMap.put(date, xPosition++);
                    }
                    int position = datePositionMap.get(date);
                    dataSet.addEntry(new Entry(position, (float) price));
                }

                XAxis xAxis = stockChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setDrawGridLines(false);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        for (Map.Entry<String, Integer> entry : datePositionMap.entrySet()) {
                            if (entry.getValue() == (int) value) {
                                return entry.getKey();
                            }
                        }
                        return "";
                    }
                });

                stockChart.getData().notifyDataChanged();
                stockChart.notifyDataSetChanged();
                stockChart.invalidate();
                Log.d("StockChart", "Graph updated successfully for last " + days + " days");
            }
            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                Log.e("StockInfo", "Error fetching stock data: " + t.getMessage());
            }
        });
    }

    private void shiftLastEntryRandomly() {
        if (dataSet == null || dataSet.getEntryCount() == 0) {
            return;
        }

        int lastIndex = dataSet.getEntryCount() - 1;
        Entry lastEntry = dataSet.getEntryForIndex(lastIndex);
        float currentPrice = lastEntry.getY();
        if (previousPrice == 0f) {
            previousPrice = currentPrice;
        }
        Random random = new Random();
        float randomShift = (random.nextFloat() * 2 - 1) * 0.2f;
        float newPrice = currentPrice + randomShift;
        lastEntry.setY(newPrice);

        float percentageChange = ((newPrice - previousPrice) / previousPrice) * 100;

        lastClosePrice_TXT.setText(String.format("%.2f", newPrice));
        precentChange_TXT.setText(String.format("%.2f%%", percentageChange));

        precentChange_TXT.setTextColor(percentageChange < 0 ? Color.RED : Color.GREEN);

        if (currentDisplayedDays == 1) {
            addNewLiveEntry(newPrice);
        }
    }
    private void addNewLiveEntry(float newPrice) {
        if (currentDisplayedDays != 1)
            return;

        float timeX = livePriceData.isEmpty() ? 0 : livePriceData.get(livePriceData.size() - 1).getX() + 1;
        livePriceData.add(new Entry(timeX, newPrice));

        updateLiveGraph();
    }

    private void updateLiveGraph() {
        if (currentDisplayedDays != 1)
            return;
        if (dataSet == null) {
            dataSet = new LineDataSet(livePriceData, "Live Stock Price");
            styleDataSet(dataSet);
            stockChart.setData(new LineData(dataSet));
        } else {
            dataSet.setValues(livePriceData);
        }
        stockChart.getData().notifyDataChanged();
        stockChart.notifyDataSetChanged();
        stockChart.invalidate();
    }
}






