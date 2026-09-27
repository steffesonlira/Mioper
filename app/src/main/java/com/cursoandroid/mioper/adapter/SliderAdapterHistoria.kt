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

class SliderAdapterHistoria(var context: Context) : PagerAdapter() {

    var layoutInflater: LayoutInflater? = null

    val slide_images = intArrayOf(
        R.drawable.historia5,
        R.drawable.historia6,
        R.drawable.historia7,
        R.drawable.historia8,
        R.drawable.historia9,
        R.drawable.logo_mioper
    )

    val slide_headings = arrayOf(
        "Capítulo 1 - Problemática",
        "Capítulo 2 - A Reunião",
        "Capítulo 3 - Discussões",
        "Capítulo 4 - Vivência no Time",
        "Capítulo 5 - A Ideia",
        "Capítulo 6 - O Mioper  "
    )

    val slide_descs = arrayOf(
        "A ideia surgiu após uma problemática, passada pelo professor da faculdade, onde teriamos que apresentar algo sobre a Indústria 4.0.",
        "De forma descontraída, três colegas reuniram-se através de um Brainstorming de ideias.",
        "Para discutir um problema alvo: O transporte coletivo, onde encontraram má qualidade, preços abusivos e em muitas das vezes, burocracia em seus contratos.",
        "Por ser parte do público alvo, essa solução seria mais viável, visto que, tinhamos essa experiência dentro do time.",
        "Pensamos... E se desenvolvessemos algo moderno, pensando no bem-estar social... Algo parecido como Uber... ",
        "E no meio dessas ideias, fomos aprimorando o projeto: E que tal se chamar MIOPER? Bem, esse nome é fácil de se explicar, nós três temos algo em comum, usamos óculos e temos miopia."
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
