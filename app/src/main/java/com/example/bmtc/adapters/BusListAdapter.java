package com.example.bmtc.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bmtc.R;
import com.example.bmtc.models.Bus;
import java.util.List;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.BusViewHolder> {

    private List<Bus> busList ;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Bus bus);
    }

    public BusListAdapter(List<Bus> busList, OnItemClickListener listener) {
        this.busList = busList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus bus=busList.get(position);
        holder  .busNumber.setText("Bus_id: " + bus.getBusId());
        holder.vehicleNumber.setText("Vehicle_Number: " + bus.getVehicleNumber());
        holder.route.setText("Route: " + bus.getRoute());
        holder.fare.setText("Fare: ₹" + bus.getFare()); // Display fare

        holder.itemView.setOnClickListener(v -> listener.onItemClick(bus));
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView busNumber, vehicleNumber, route, fare;

        public BusViewHolder(View itemView) {
            super(itemView);
            busNumber = itemView.findViewById(R.id.textBusNumber);
            vehicleNumber = itemView.findViewById(R.id.textVehicleNumber);
            route = itemView.findViewById(R.id.textRoute);
            fare = itemView.findViewById(R.id.textFare); // New TextView for fare
        }
    }
}
