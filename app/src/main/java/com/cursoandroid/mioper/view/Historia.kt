package com.cursoandroid.mioper.view

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.adapter.SliderAdapterHistoria

class Historia : AppCompatActivity() {

    private lateinit var vSlidePager: ViewPager
    private lateinit var lDotLayout: LinearLayout
    private lateinit var btnAnterior: Button
    private lateinit var btnProximo: Button
    private lateinit var mDots: Array<TextView>

    private var mCurrentPage = 0

    private lateinit var sliderAdapterHistoria: SliderAdapterHistoria

    private val viewListener = object : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(i: Int, v: Float, il: Int) {
        }

        override fun onPageSelected(i: Int) {
            addDotsIndicator(i)
            mCurrentPage = i

            if (i == 0) {
                btnProximo.isEnabled = true
                btnAnterior.isEnabled = false
                btnAnterior.visibility = View.INVISIBLE

                btnProximo.text = "Próximo"
                btnAnterior.text = ""
            } else if (i == mDots.size - 1) {
                btnProximo.isEnabled = true
                btnAnterior.isEnabled = true
                btnAnterior.visibility = View.VISIBLE

                btnProximo.text = "Fim"
                btnAnterior.text = "Anterior"
            } else {
                btnProximo.isEnabled = true
                btnAnterior.isEnabled = true
                btnAnterior.visibility = View.VISIBLE

                btnProximo.text = "Próximo"
                btnAnterior.text = "Anterior"
            }
        }

        override fun onPageScrollStateChanged(i: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historia)

        vSlidePager = findViewById(R.id.slideViewPagerHistoria)
        lDotLayout = findViewById(R.id.dotsLayoutHistoria)

        btnAnterior = findViewById(R.id.btnAnteriorHistoria)
        btnProximo = findViewById(R.id.btnProximoHistoria)
        sliderAdapterHistoria = SliderAdapterHistoria(this)

        vSlidePager.adapter = sliderAdapterHistoria

        addDotsIndicator(0)

        vSlidePager.addOnPageChangeListener(viewListener)

        btnProximo.setOnClickListener { vSlidePager.currentItem = mCurrentPage + 1 }

        btnAnterior.setOnClickListener { vSlidePager.currentItem = mCurrentPage - 1 }
    }

    fun addDotsIndicator(position: Int) {
        lDotLayout.removeAllViews()

        mDots = Array(6) {
            TextView(this).apply {
                text = Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_COMPACT)
                textSize = 45f
                setTextColor(resources.getColor(R.color.transparent2))
                lDotLayout.addView(this)
            }
        }
        if (mDots.isNotEmpty()) {
            mDots[position].setTextColor(resources.getColor(R.color.DarkCyan))
        }
    }
}
