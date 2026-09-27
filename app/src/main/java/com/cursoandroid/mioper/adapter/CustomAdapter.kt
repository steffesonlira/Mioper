package com.cursoandroid.mioper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cursoandroid.mioper.model.CustomItem;
import com.cursoandroid.mioper.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<CustomItem> {

    public CustomAdapter(@NonNull Context context, ArrayList<CustomItem> customList){
        super(context, 0, customList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }
        CustomItem item = getItem(position);
        ImageView spinnerIV = convertView.findViewById(R.id.ivSpinnerLayout);
        TextView spinnerTV = convertView.findViewById(R.id.tvSpinnerLayout);
        if (item != null) {
            spinnerIV.setImageResource(item.getSpinnerItemImage());
            spinnerTV.setText(item.getSppinerItemName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dropdown_layout, parent, false);
        }
        CustomItem item = getItem(position);
        ImageView dropdownIV = convertView.findViewById(R.id.ivDropdownLayout);
        TextView dropdownTV = convertView.findViewById(R.id.tvDropdownLayout);
        if (item != null) {
            dropdownIV.setImageResource(item.getSpinnerItemImage());
            dropdownTV.setText(item.getSppinerItemName());
        }
        return convertView;
    }
}
