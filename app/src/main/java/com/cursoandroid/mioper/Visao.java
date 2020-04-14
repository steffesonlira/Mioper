package com.cursoandroid.mioper;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class Visao extends AppCompatActivity {

    private ViewPager vSlidePager;
    private LinearLayout lDotLayout;
    private Button btnAnterior;
    private Button btnProximo;
    private TextView[] mDots;

    private int mCurrentPage;

    private SliderAdapterVisao sliderAdapterVisao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visao);

        vSlidePager = findViewById(R.id.slideViewPagerVisao);
        lDotLayout  = findViewById(R.id.dotsLayoutVisao);

        btnAnterior = findViewById(R.id.btnAnteriorVisao);
        btnProximo = findViewById(R.id.btnProximoVisao);
        sliderAdapterVisao= new SliderAdapterVisao(this);

        vSlidePager.setAdapter(sliderAdapterVisao);

        addDotsIndicator(0);

        vSlidePager.addOnPageChangeListener(viewListener);

        //Onclick botão next e back slider
        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vSlidePager.setCurrentItem(mCurrentPage + 1);
            }
        });

        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vSlidePager.setCurrentItem(mCurrentPage - 1);
            }
        });

    }
    public void addDotsIndicator(int position){
        mDots = new TextView[2];
        lDotLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_COMPACT));
            mDots[i].setTextSize(45);
            mDots[i].setTextColor(getResources().getColor(R.color.transparent2));

            lDotLayout.addView(mDots[i]);
        }
        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.DarkCyan));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int i, float v, int il) {

        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);
            mCurrentPage = i;

            if(i ==0){
                btnProximo.setEnabled(true);
                btnAnterior.setEnabled(false);
                btnAnterior.setVisibility(View.INVISIBLE);

                btnProximo.setText("Próximo");
                btnAnterior.setText("");

            }else if (i == mDots.length - 1){
                btnProximo.setEnabled(true);
                btnAnterior.setEnabled(true);
                btnAnterior.setVisibility(View.VISIBLE);

                btnProximo.setText("Fim");
                btnAnterior.setText("Anterior");
            }else{
                btnProximo.setEnabled(true);
                btnAnterior.setEnabled(true);
                btnAnterior.setVisibility(View.VISIBLE);

                btnProximo.setText("Próximo");
                btnAnterior.setText("Anterior");
            }

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
