package com.example.bmtc.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bmtc.R;
import com.example.bmtc.models.Ticket;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> ticketList;

    public TicketAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.busNumber.setText("Bus: " + ticket.getBusNumber());
        holder.vehicleNumber.setText("Vehicle: " + ticket.getVehicleNumber());
        holder.startStop.setText("From: " + ticket.getStartStop());
        holder.endStop.setText("To: " + ticket.getEndStop());
        holder.fare.setText("Fare: ₹" + ticket.getFare());
        holder.dateTime.setText(ticket.getDateTime());
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView busNumber, vehicleNumber, startStop, endStop, fare, dateTime;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            busNumber = itemView.findViewById(R.id.tvBusNumber);
            vehicleNumber = itemView.findViewById(R.id.tvVehicleNumber);
            startStop = itemView.findViewById(R.id.tvStartStop);
            endStop = itemView.findViewById(R.id.tvEndStop);
            fare = itemView.findViewById(R.id.tvFare);
            dateTime = itemView.findViewById(R.id.tvDateTime);
        }
    }
}
