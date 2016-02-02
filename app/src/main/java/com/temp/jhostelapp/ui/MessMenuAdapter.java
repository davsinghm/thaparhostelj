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
public class MessMenuAdapter extends RecyclerView.Adapter<MessMenuAdapter.ViewHolder> {

    public Context context;
    public ArrayList<MessMeal> arrayList = new ArrayList<>();

    public MessMenuAdapter(ArrayList<MessMeal> arrayList) {
        this.arrayList = arrayList;
    }

    public void setArrayList(ArrayList<MessMeal> arrayList) {
        this.arrayList = arrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView line1;
        public TextView line2;

        public ViewHolder(final View view) {
            super(view);

            CardView cardView = (CardView) view.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.title);
            line1 = (TextView) view.findViewById(R.id.line1);
            line2 = (TextView) view.findViewById(R.id.line2);

        }

        @Override
        public void onClick(View v) {
            /*line1.setMaxLines(expanded ? 1 : Integer.MAX_VALUE);
            expanded = !expanded;
            notifyDataSetChanged();*/
        }
    }

    @Override
    public MessMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context = parent.getContext()).inflate(R.layout.list_item_noti, parent, false));
    }

    @Override
    public void onBindViewHolder(final MessMenuAdapter.ViewHolder viewHolder, final int position) {

        MessMeal meal = arrayList.get(position);
        viewHolder.title.setText(meal.getMeal());
        viewHolder.line1.setText(meal.getMenu());
        //viewHolder.line2.setText(noti.getTimestamp() + "");

    }

    @Override
    public int getItemCount() {
        if (arrayList != null)
            return arrayList.size();
        return 0;
    }

}
