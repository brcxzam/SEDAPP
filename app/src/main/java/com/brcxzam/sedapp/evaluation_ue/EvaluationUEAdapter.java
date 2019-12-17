package com.brcxzam.sedapp.evaluation_ue;

import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.database.Anexo21;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EvaluationUEAdapter extends RecyclerView.Adapter<EvaluationUEAdapter.ViewHolder> {

    private List<Anexo21> anexo21ArrayList;
    private OnItemClickListener listener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setAnexo21ArrayList(List<Anexo21> anexo21ArrayList) {
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
        Anexo21 data = anexo21ArrayList.get(position);
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipelayout, data.getId());
        viewBinderHelper.closeLayout(data.getId());
        holder.bindData(data.getPeriodo(),data.getRazon_social(), data.getTotal(),data.getFecha());
    }

    @Override
    public int getItemCount() {
        return anexo21ArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView period, name, total, date;
        private SwipeRevealLayout swipelayout;
        int errorColor = Color.rgb(244,67,54);
        int warningColor = Color.rgb(255,152,0);
        int successColor = Color.rgb(128,237,153);
        ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            period = itemView.findViewById(R.id.period);
            name = itemView.findViewById(R.id.name);
            total = itemView.findViewById(R.id.total);
            date = itemView.findViewById(R.id.date);
            LinearLayout delete = itemView.findViewById(R.id.delete);
            swipelayout = itemView.findViewById(R.id.swipelayout);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                            Anexo21 data = anexo21ArrayList.get(position);
                            viewBinderHelper.closeLayout(data.getId());
                        }
                    }
                }
            });
        }
        void bindData(String period, String name, double total, String date) {
            DecimalFormat format = new DecimalFormat("#.00");
            String[] dateArray = date.split("-");
            this.period.setText(period);
            this.name.setText(name);
            this.total.setText(String.valueOf(format.format(total))+"%");
            this.date.setText(dateFormat(Integer.valueOf(dateArray[0]),Integer.valueOf(dateArray[1]),Integer.valueOf(dateArray[2])));
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
        private String dateFormat(int year, int month, int dayOfMonth) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, dayOfMonth, 0, 0, 0);

            Date chosenDate = cal.getTime();

            DateFormat dateLong = DateFormat.getDateInstance(DateFormat.LONG, Locale.forLanguageTag("spa"));
            return dateLong.format(chosenDate);
        }
    }
}
