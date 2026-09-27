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

class SliderAdapterDesign(var context: Context) : PagerAdapter() {

    var layoutInflater: LayoutInflater? = null

    val slide_images = intArrayOf(
        R.drawable.design5,
        R.drawable.design6,
        R.drawable.design7,
        R.drawable.design8,
        R.drawable.design9,
        R.drawable.design10
    )

    val slide_headings = arrayOf(
        "O Design Thinking",
        "Adaptando-se ao Mioper",
        "Imersão",
        "Ideação",
        "Prototipação",
        "Implementação"
    )

    val slide_descs = arrayOf(
        "O Design Thinking está associado pela forma como resolvemos problemas, para desenvolver uma solução que atenda uma problemática natural",
        "Nós Miopers, seguimos essa metodologia, trazendo uma experiência  por meio de dificuldades estudantis, para a locomoção de pessoas de forma compartilhada",
        "Para seguir a metodologia, inciamos com a Imersão, onde conhecemos os problemas de um público alvo.",
        "Depois, usamos a Ideação, realizando reuniões, para levantar ideias, pensando em atingir os objetivos.",
        "Depois partindo para a Prototipação, realizando uma construção de cronogramas e tecnologias para desenvolvimento.",
        "E por fim, utilizamos a Implementação, para colocar tudo o que foi visto na palma da mão dos nossos usuários."
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
