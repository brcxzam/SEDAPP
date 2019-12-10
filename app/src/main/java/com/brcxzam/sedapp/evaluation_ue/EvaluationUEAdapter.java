package com.brcxzam.sedapp.evaluation_ue;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllAnexo_2_1Query;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

public class EvaluationUEAdapter extends RecyclerView.Adapter<EvaluationUEAdapter.ViewHolder> {

    private List<ReadAllAnexo_2_1Query.Anexo_2_1> anexo21ArrayList;
    private OnItemClickListener listener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onEditClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    EvaluationUEAdapter(List<ReadAllAnexo_2_1Query.Anexo_2_1> anexo21ArrayList) {
        this.anexo21ArrayList = anexo21ArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReadAllAnexo_2_1Query.Anexo_2_1 data = anexo21ArrayList.get(position);
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipelayout, data.id());
        viewBinderHelper.closeLayout(data.id());
        holder.bindData(data.periodo(),data.UE().razon_social(), data.total());
    }

    @Override
    public int getItemCount() {
        return anexo21ArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView period, name;
        private LinearLayout edit, delete;
        private SwipeRevealLayout swipelayout;
        int errorColor = Color.rgb(244,67,54);
        int warningColor = Color.rgb(255,152,0);
        int successColor = Color.rgb(128,237,153);
        ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            period = itemView.findViewById(R.id.period);
            name = itemView.findViewById(R.id.name);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            swipelayout = itemView.findViewById(R.id.swipelayout);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                            ReadAllAnexo_2_1Query.Anexo_2_1 data = anexo21ArrayList.get(position);
                            viewBinderHelper.closeLayout(data.id());
                        }
                    }
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                            ReadAllAnexo_2_1Query.Anexo_2_1 data = anexo21ArrayList.get(position);
                            viewBinderHelper.closeLayout(data.id());
                        }
                    }
                }
            });
        }
        void bindData(String period, String name, double total) {
            this.period.setText(period);
            this.name.setText(name);
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
    }
}
