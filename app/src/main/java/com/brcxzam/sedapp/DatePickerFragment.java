package com.brcxzam.sedapp;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public DatePickerFragment(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog pickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),onDateSetListener,year,month,day);
        pickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return pickerDialog;
    }

}
