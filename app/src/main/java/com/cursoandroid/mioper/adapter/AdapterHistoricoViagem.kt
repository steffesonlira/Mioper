package com.cursoandroid.mioper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.model.HistoricoUsuario

class AdapterHistoricoViagem(
    private val listaUsuario: MutableList<HistoricoUsuario>
) : RecyclerView.Adapter<AdapterHistoricoViagem.MyViewHolder>() {

    init {
        // Viagem mais recente no topo do RecyclerView
        listaUsuario.reverse()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemLista = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_lista, viewGroup, false)
        return MyViewHolder(itemLista)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val historico = listaUsuario[i]

        myViewHolder.cidade.text = historico.cidade
        myViewHolder.rua.text = historico.rua
        myViewHolder.data.text = historico.data
    }

    override fun getItemCount(): Int = listaUsuario.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cidade: TextView = itemView.findViewById(R.id.textViewCidade)
        val rua: TextView = itemView.findViewById(R.id.textViewRua)
        val data: TextView = itemView.findViewById(R.id.textViewData)
    }
}
