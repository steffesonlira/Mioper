package com.cursoandroid.mioper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase

class AdapterMetodosPagamentos(
    private val tipoPagamentoOK: MutableList<String>
) : RecyclerView.Adapter<AdapterMetodosPagamentos.ViewHolder>() {

    var userMailReplaced: String? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_lista_pagamentos, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.metodoPagamento.text = tipoPagamentoOK[i]

        viewHolder.deletarNumero.setOnClickListener {
            val firebaseUser = UsuarioFirebase.getUsuarioAtual()!!
            val userMailReplaced = firebaseUser.email!!.replace('.', '-')

            ConfiguracaoFirebase.getFirebaseDatabase()
                .child("CartoesPagamentos")
                .child(userMailReplaced)
                .child(tipoPagamentoOK[i])
                .removeValue()

            tipoPagamentoOK.clear()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = tipoPagamentoOK.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val metodoPagamento: TextView = itemView.findViewById(R.id.listaPagamentosID)
        val deletarNumero: Button = itemView.findViewById(R.id.removerCartaoID)
    }

    companion object {
        private const val TAg = "RecyclerViewAdapter"
    }
}
