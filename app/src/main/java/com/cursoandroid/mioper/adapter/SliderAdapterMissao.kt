package com.cursoandroid.mioper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.cursoandroid.mioper.R

class SliderAdapterMissao(var context: Context) : PagerAdapter() {

    var layoutInflater: LayoutInflater? = null

    val slide_images = intArrayOf(
        R.drawable.missao1,
        R.drawable.missao2
    )

    val slide_headings = arrayOf(
        "",
        ""
    )

    val slide_descs = arrayOf(
        "Agenciar demandas entre passageiros e profissionais de transporte",
        "Com o objetivo de oferecer serviços de qualidade com o melhor custo benefício."
    )

    override fun getCount(): Int = slide_headings.size

    override fun isViewFromObject(view: View, o: Any): Boolean = view == o as RelativeLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slide_manifesto, container, false)

        val slideImageView = view.findViewById<ImageView>(R.id.imgManifesto1)
        val slideHeading = view.findViewById<TextView>(R.id.txtManifesto1)
        val slideDescription = view.findViewById<TextView>(R.id.txtManifesto1Desc1)

        slideImageView.setImageResource(slide_images[position])
        slideHeading.text = slide_headings[position]
        slideDescription.text = slide_descs[position]

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}
