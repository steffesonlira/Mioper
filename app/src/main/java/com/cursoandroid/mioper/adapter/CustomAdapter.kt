package com.cursoandroid.mioper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.model.CustomItem

class CustomAdapter(context: Context, customList: List<CustomItem>) :
    ArrayAdapter<CustomItem>(context, 0, customList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.custom_spinner_layout, parent, false)
        val spinnerIV = view.findViewById<ImageView>(R.id.ivSpinnerLayout)
        val spinnerTV = view.findViewById<TextView>(R.id.tvSpinnerLayout)
        getItem(position)?.let { item ->
            spinnerIV.setImageResource(item.spinnerItemImage)
            spinnerTV.text = item.sppinerItemName
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.custom_dropdown_layout, parent, false)
        val dropdownIV = view.findViewById<ImageView>(R.id.ivDropdownLayout)
        val dropdownTV = view.findViewById<TextView>(R.id.tvDropdownLayout)
        getItem(position)?.let { item ->
            dropdownIV.setImageResource(item.spinnerItemImage)
            dropdownTV.text = item.sppinerItemName
        }
        return view
    }
}
