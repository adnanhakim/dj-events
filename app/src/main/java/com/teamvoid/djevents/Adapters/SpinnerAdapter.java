package com.teamvoid.djevents.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.teamvoid.djevents.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {
    Typeface typeface;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> items) {
        super(context, resource, items);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            typeface = getContext().getResources().getFont(R.font.montserrat);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            view.setTypeface(typeface);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            view.setTypeface(typeface);
        return view;
    }
}
