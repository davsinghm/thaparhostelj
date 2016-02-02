package com.temp.jhostelapp.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.temp.jhostelapp.R;

import java.util.ArrayList;

/**
 * Created by DSM_ on 1/30/16.
 */
public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    public Context context;
    public ArrayList<Complaint> arrayList = new ArrayList<>();

    public ComplaintAdapter(ArrayList<Complaint> arrayList) {
        this.arrayList = arrayList;
    }

    public void setArrayList(ArrayList<Complaint> arrayList) {
        this.arrayList = arrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView category;
        public TextView status;
        public TextView complaint;
        public TextView timestamp;
        public CardView cardView;

        public ViewHolder(final View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
            category = (TextView) view.findViewById(R.id.category);
            complaint = (TextView) view.findViewById(R.id.complaint);
            status = (TextView) view.findViewById(R.id.status);
            timestamp = (TextView) view.findViewById(R.id.timestamp);

        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public ComplaintAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context = parent.getContext()).inflate(R.layout.list_item_complaint, parent, false));
    }

    @Override
    public void onBindViewHolder(ComplaintAdapter.ViewHolder viewHolder, final int position) {

        Complaint complaint = arrayList.get(position);
        viewHolder.category.setText(complaint.getCategory());
        viewHolder.complaint.setText(complaint.getComplaint());
        viewHolder.timestamp.setText(String.valueOf(complaint.getStartTimestamp()));
        viewHolder.status.setText(getStatus(complaint.getStatus()));
        String color = getStatusColor(complaint.getStatus());
        viewHolder.cardView.setCardBackgroundColor(Color.parseColor(color));

    }

    private String getStatusColor(String string) {
        switch (string) {

            case "UNRESOLVED":
                return "#FFC107";
            case "FWDED_PROCTOR":
                return "#FF9800";
            case "FWDED_WARDEN":
                return "#F44336";
            case "RESOLVED":
                return "#8BC34A";
            case "REGISTERED":
            default:
                return "#FFFFFF";
        }
    }

    private String getStatus(String string) {
        switch (string) {
            case "REGISTERED":
                return "Registered";
            case "UNRESOLVED":
                return "Unresolved";
            case "FWDED_PROCTOR":
                return "Forwarded to Proctor";
            case "FWDED_WARDEN":
                return "Forwarded to Warden";
            case "RESOLVED":
                return "Resolved";
            default:
                return "Unknown";
        }
    }

    @Override
    public int getItemCount() {
        if (arrayList != null)
            return arrayList.size();
        return 0;
    }

}
