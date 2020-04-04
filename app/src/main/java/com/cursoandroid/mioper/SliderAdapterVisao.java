package com.cursoandroid.mioper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapterVisao extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapterVisao(Context context){
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.drawable.visao1,
            R.drawable.visao2
    };

    public String[] slide_headings = {

            "Ser Líder:",
            "Entrega Responsável:"
    };

    public String[] slide_descs = {

            "Ser a empresa líder no segmento de transporte coletivo",
            "Ser uma empresa que seja refer~encia na entrega de valores ao cliente"
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == (RelativeLayout) o;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_manifesto, container, false);

        ImageView slideImageView =  view.findViewById(R.id.imgManifesto1);
        TextView slideHeading = view.findViewById(R.id.txtManifesto1);
        TextView slideDescription = view.findViewById(R.id.txtManifesto1Desc1);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((RelativeLayout)object);
    }
}
