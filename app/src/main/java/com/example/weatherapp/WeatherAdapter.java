package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherModal> weatherList;

    public WeatherAdapter(Context context, ArrayList<WeatherModal> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_recycle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherModal modal = weatherList.get(position);
        holder.temperature.setText(modal.getTemperature()+"°C");
        Picasso.get().load("http:".concat(modal.getIconUrl())).into(holder.condition);
        holder.wind.setText(modal.getWindSpeed()+" kph");
        SimpleDateFormat insdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat outsdf = new SimpleDateFormat("hh:mm aa");
        try {
            Date d = insdf.parse(modal.getTime());
            holder.time.setText(outsdf.format(d));
        }catch (ParseException e){
            System.out.println(e);
        }
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView wind, temperature, time;
        private ImageView condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wind = itemView.findViewById(R.id.wind_speed);
            temperature = itemView.findViewById(R.id.temperature);
            time = itemView.findViewById(R.id.time);
            condition = itemView.findViewById(R.id.condition);
        }
    }
}
