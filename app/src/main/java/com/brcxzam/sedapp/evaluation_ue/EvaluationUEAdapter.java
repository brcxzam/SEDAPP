package com.brcxzam.sedapp.evaluation_ue;

import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.database.Anexo21;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EvaluationUEAdapter extends RecyclerView.Adapter<EvaluationUEAdapter.ViewHolder> implements Filterable {

    private List<Anexo21> anexo21List;
    private List<Anexo21> anexo21ListFiltered;
    private OnItemClickListener listener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String constraintString = constraint.toString();
                if (constraintString.isEmpty()){
                    anexo21ListFiltered = anexo21List;
                } else {
                    List<Anexo21> filteredList = new ArrayList<>();
                    for (Anexo21 row : anexo21List) {
                        if (row.getRazon_social().toLowerCase().contains(constraintString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    anexo21ListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = anexo21ListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                anexo21ListFiltered = (ArrayList<Anexo21>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onItemClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    void setAnexo21List(List<Anexo21> anexo21List) {
        this.anexo21List = anexo21List;
        this.anexo21ListFiltered = anexo21List;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Anexo21 data = anexo21ListFiltered.get(position);
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeLayout, data.getId());
        viewBinderHelper.closeLayout(data.getId());
        holder.bindData(data.getPeriodo(),data.getRazon_social(), data.getTotal(),data.getFecha());
    }

    @Override
    public int getItemCount() {
        return anexo21ListFiltered.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView period, name, total, month, day, year;
        private SwipeRevealLayout swipeLayout;
        int errorColor = Color.parseColor("#F8625A");
        int warningColor = Color.parseColor("#FFD253");
        int successColor = Color.parseColor("#7AD3B9");
        ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            period = itemView.findViewById(R.id.period);
            name = itemView.findViewById(R.id.name);
            total = itemView.findViewById(R.id.total);
            month = itemView.findViewById(R.id.month);
            day = itemView.findViewById(R.id.day);
            year = itemView.findViewById(R.id.year);
            swipeLayout = itemView.findViewById(R.id.swipelayout);
            MaterialCardView delete = itemView.findViewById(R.id.delete);
            MaterialCardView mainLayout = itemView.findViewById(R.id.main_layout);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                            Anexo21 data = anexo21ListFiltered.get(position);
                            viewBinderHelper.closeLayout(data.getId());
                        }
                    }
                }
            });
            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            Anexo21 data = anexo21ListFiltered.get(position);
                            viewBinderHelper.closeLayout(data.getId());
                        }
                    }
                }
            });
        }
        void bindData(String period, String name, double total, String date) {
            DecimalFormat format = new DecimalFormat("#.00");
            String[] dateArray = dateFormat(date);
            String totalString = format.format(total)+"%";
            this.period.setText(period);
            this.name.setText(name);
            this.total.setText(totalString);
            this.month.setText(dateArray[0]);
            this.day.setText(dateArray[1]);
            this.year.setText(dateArray[2]);
            if (total <= 45) {
                this.icon.setImageResource(R.drawable.ic_cancel_black_24dp);
                this.icon.setColorFilter(errorColor);
            } else if (total <= 66) {
                this.icon.setImageResource(R.drawable.baseline_check_circle_outline_white_48dp);
                this.icon.setColorFilter(warningColor);
            } else if (total <= 100) {
                this.icon.setImageResource(R.drawable.ic_check_circle_black_24dp);
                this.icon.setColorFilter(successColor);
            }
        }
        private String[] dateFormat(String dtStart) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            try {
                date = format.parse(dtStart);
            } catch (ParseException e) {
                Log.d("ERROR PARSE DATE", Objects.requireNonNull(e.getMessage()));
            }
            SimpleDateFormat month = new SimpleDateFormat("MMM", Locale.forLanguageTag("spa"));
            String monthString = month.format(date).toUpperCase();
            SimpleDateFormat day = new SimpleDateFormat("dd", Locale.forLanguageTag("spa"));
            String dayString = day.format(date);
            SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.forLanguageTag("spa"));
            String yearString = year.format(date);
            return new String[]{monthString,dayString,yearString};
        }
    }
}
