package com.brcxzam.sedapp.evaluation_ue;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllAnexo_2_1Query;

import java.util.List;

public class EvaluationUEAdapter extends RecyclerView.Adapter<EvaluationUEAdapter.ViewHolder> {

    List<ReadAllAnexo_2_1Query.Anexo_2_1> anexo21ArrayList;

    public EvaluationUEAdapter(List<ReadAllAnexo_2_1Query.Anexo_2_1> anexo21ArrayList) {
        this.anexo21ArrayList = anexo21ArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReadAllAnexo_2_1Query.Anexo_2_1 data = anexo21ArrayList.get(position);
        holder.setData(data.periodo(),data.UE().razon_social(),data.total());
    }

    @Override
    public int getItemCount() {
        return anexo21ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView period, name;
        int errorColor = Color.rgb(244,67,54);
        int warningColor = Color.rgb(255,152,0);
        int successColor = Color.rgb(128,237,153);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            period = itemView.findViewById(R.id.period);
            name = itemView.findViewById(R.id.name);
        }
        public void setData(String period, String name, int total) {
            this.period.setText(period);
            this.name.setText(name);
            if (total <= 16) {
                this.icon.setImageResource(R.drawable.ic_cancel_black_24dp);
                this.icon.setColorFilter(errorColor);
            } else if (total <= 24) {
                this.icon.setImageResource(R.drawable.baseline_check_circle_outline_white_48dp);
                this.icon.setColorFilter(warningColor);
            } else if (total <= 35) {
                this.icon.setImageResource(R.drawable.ic_check_circle_black_24dp);
                this.icon.setColorFilter(successColor);
            }
        }
    }
}
