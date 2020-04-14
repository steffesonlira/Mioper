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
            R.drawable.design5,
            R.drawable.design6,
            R.drawable.design7,
            R.drawable.design8,
            R.drawable.design9,
            R.drawable.design10
    };

    public String[] slide_headings = {

            "O Design Thinking",
            "Adaptando-se ao Mioper",
            "Imersão",
            "Ideação",
            "Prototipação",
            "Implementação"
    };

    public String[] slide_descs = {

            "O Design Thinking está associado pela forma como resolvemos problemas, para desenvolver uma solução que" +
                    " atenda uma problemática natural",
            "Nós Miopers, seguimos essa metodologia, trazendo uma experiência  por meio de dificuldades estudantis, para a " +
                    "locomoção de pessoas de forma compartilhada",
            "Para seguir a metodologia, inciamos com a Imersão, onde conhecemos os problemas de um público alvo.",
            "Depois, usamos a Ideação, realizando reuniões, para levantar ideias, pensando em atingir os objetivos.",
            "Depois partindo para a Prototipação, realizando uma construção de cronogramas e tecnologias para desenvolvimento.",
            "E por fim, utilizamos a Implementação, para colocar tudo o que foi visto na palma da mão dos nossos usuários."
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
