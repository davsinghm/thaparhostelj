package com.temp.jhostelapp.ui;

import android.content.Context;
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

        public ViewHolder(final View view) {
            super(view);

            CardView cardView = (CardView) view.findViewById(R.id.cardView);
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
    public void onBindViewHolder(final ComplaintAdapter.ViewHolder viewHolder, final int position) {

        Complaint complaint = arrayList.get(position);
        viewHolder.category.setText(complaint.getCategory());
        viewHolder.complaint.setText(complaint.getComplaint());
        viewHolder.timestamp.setText(String.valueOf(complaint.getStartTimestamp()));
        viewHolder.status.setText(complaint.getStatus());

    }

    @Override
    public int getItemCount() {
        if (arrayList != null)
            return arrayList.size();
        return 0;
    }

}
