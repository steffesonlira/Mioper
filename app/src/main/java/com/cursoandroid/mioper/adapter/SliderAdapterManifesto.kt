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

class SliderAdapterManifesto(var context: Context) : PagerAdapter() {

    var layoutInflater: LayoutInflater? = null

    val slide_images = intArrayOf(
        R.drawable.manifesto6,
        R.drawable.manifesto7,
        R.drawable.manifesto8,
        R.drawable.manifesto9,
        R.drawable.logo
    )

    val slide_headings = arrayOf(
        "A Cooperatividade",
        "Levantar Ideias",
        "Utilização de Recursos",
        "Aprimoramento",
        "Mioper"
    )

    val slide_descs = arrayOf(
        "A nossa metodologia surgiu-se na cooperatividade de uma equipe que estava aprendendo.",
        "A partir do levantamento de ideias para um projeto acadêmico, adaptando-se ao dia-a-dia.",
        "Utilizou-se recursos que auxiliaram na resolução e na iniciação das atividades.",
        "Que foi sendo aprimorada a cada semestre estudantil.",
        "Daí surgiu uma ideia inovadora visando um futuro próximo no modo de deslocar-se de forma compartilhada."
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
