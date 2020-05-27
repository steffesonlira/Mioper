package com.cursoandroid.mioper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.cursoandroid.mioper.R;

public class SliderAdapterHistoria extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapterHistoria(Context context){
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.drawable.historia5,
            R.drawable.historia6,
            R.drawable.historia7,
            R.drawable.historia8,
            R.drawable.historia9,
            R.drawable.logo_mioper,

    };

    public String[] slide_headings = {

            "Capítulo 1 - Problemática",
            "Capítulo 2 - A Reunião",
            "Capítulo 3 - Discussões",
            "Capítulo 4 - Vivência no Time",
            "Capítulo 5 - A Ideia",
            "Capítulo 6 - O Mioper  "

    };

    public String[] slide_descs = {

            "A ideia surgiu após uma problemática, passada pelo professor da faculdade, onde teriamos que apresentar algo " +
                    "sobre a Indústria 4.0.",
            "De forma descontraída, três colegas reuniram-se através de um Brainstorming de ideias.",
            "Para discutir um problema alvo: O transporte coletivo, onde encontraram má qualidade, preços " +
                    "abusivos e em muitas das vezes, burocracia em seus contratos.",
            "Por ser parte do público alvo, essa solução seria mais viável, visto que, tinhamos essa experiência dentro do time.",
            "Pensamos... E se desenvolvessemos algo moderno, pensando no bem-estar social... Algo parecido como Uber... ",
            "E no meio dessas ideias, fomos aprimorando o projeto: E que tal se chamar MIOPER? Bem, esse nome é fácil de se explicar," +
                    " nós três temos algo em comum, usamos óculos e temos miopia."

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
