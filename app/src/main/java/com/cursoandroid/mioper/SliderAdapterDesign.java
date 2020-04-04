package com.cursoandroid.mioper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapterDesign extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapterDesign(Context context){
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.drawable.design1,
            R.drawable.design2,
            R.drawable.design3,
            R.drawable.design4
    };

    public String[] slide_headings = {

            "Design Thinking 1",
            "Design Thinking 2",
            "Design Thinking 3",
            "Design Thinking 4"
    };

    public String[] slide_descs = {

            "Loremdhsfhjdjkfdjifhjksfhsjfkjeskfkljdshfjdhfdthhdhgdhghgfhghhghdfbhgdxgfxcbfghfdghdfgfdhfdgsddfwarfesg  "+
                    "jjgfdkjg",
            "sdfhdugyesijfsdlifjaijfsdiursjehgkdshjdhgkjjjkgdgjfdlkigudkljglixfjgksjgilu jfksdjvjikdsjgk jlkdfjgl jjig"+
                    "fdjgkfhgh",
            "zuxhfdskjaeolkjdzlfjsekh jdjkashk hkejeji fdji jjij khjikh khu hhidishfkdnvksdhfjebvj hhwekjh khnjkhjhbjm"+
                    "kjxfbhkjfg" +
                    "rd",
            "zuxhfdskjaeolkjdzlfjsekh jdjkashk hkejeji fdji jjij khjikh khu hhidishfkdnvksdhfjebvj hhwekjh khnjkhjhbjm"+
                    "kjxfbhkjfg" +
                    "rd"
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
