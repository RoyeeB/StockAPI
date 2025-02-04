package com.example.stockapi;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stocklib.Stock;

import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StockViewHolder> {

    private List<Stock> stocksList;

    public StocksAdapter(List<Stock> stocksList) {
        this.stocksList = stocksList;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_card, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stocksList.get(position);
        holder.companyName.setText(stock.getCompanyName());
        holder.symbol.setText(stock.getName());
        holder.lastClosePrice.setText("$" + String.format("%.2f", stock.getLastClosingPrice()));
        Glide.with(holder.logo.getContext())
                .load(stock.getLogo())
                .into(holder.logo);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), StockActivity.class);
            intent.putExtra("stockSymbol", stock.getCompanyName());
            v.getContext().startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return stocksList.size();
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView companyName, symbol, lastClosePrice;
        ImageView logo;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.companyName_TXT);
            symbol = itemView.findViewById(R.id.companySymbol_TXT);
            lastClosePrice = itemView.findViewById(R.id.lastClosePrice_TXT);
            logo = itemView.findViewById(R.id.logo_IMG);
        }

    }

    public void addStock(Stock stock) {
        this.stocksList.add(stock);
        notifyItemInserted(stocksList.size() - 1);
    }

}
