package com.cursoandroid.mioper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapterManifesto extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapterManifesto(Context context){
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.drawable.manifesto6,
            R.drawable.manifesto7,
            R.drawable.manifesto8,
            R.drawable.manifesto9,
            R.drawable.logo
    };

    public String[] slide_headings = {

            "A Cooperatividade",
            "Levantar Ideias",
            "Utilização de Recursos",
            "Aprimoramento",
            "Mioper"
    };

    public String[] slide_descs = {

            "A nossa metodologia surgiu-se na cooperatividade de uma equipe que estava aprendendo.",
            "A partir de levantamentos de ideias para um projeto acadêmico onde experiências no dia a dia amadureceu a ideia.",
            "Utilizando recursos que auxiliaram na resolução e na iniciação das atividades.",
            "Que foi sendo aprimorada a cada semestre estudantil.",
            "Daí surgiu uma ideia inovadora visando um futuro próximo no modo de deslocar-se de forma compartilhada."
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
