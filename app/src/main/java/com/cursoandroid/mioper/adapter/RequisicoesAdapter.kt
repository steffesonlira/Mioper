package com.cursoandroid.mioper.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.model.Local
import com.cursoandroid.mioper.model.Requisicao
import com.cursoandroid.mioper.model.UserProfile
import com.google.android.gms.maps.model.LatLng

class RequisicoesAdapter(
    private val requisicoes: List<Requisicao>,
    private val context: Context,
    private val motorista: UserProfile?
) : RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.adapter_requisicoes, parent, false)
        return MyViewHolder(item)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val requisicao = requisicoes[position]
        val passageiro = requisicao.passageiro!!
        val destino = requisicao.destino

        holder.nome.text = "Passageiro: " + passageiro.name
        try {
            if (motorista != null) {
                val localPassageiro = LatLng(
                    passageiro.latitude!!.toDouble(),
                    passageiro.longitude!!.toDouble()
                )

                val localMotorista = LatLng(
                    motorista.latitude!!.toDouble(),
                    motorista.longitude!!.toDouble()
                )

                val distancia = Local.calcularDistancia(localPassageiro, localMotorista)
                val distanciaFormatada = Local.formatarDistancia(distancia)
                holder.endereco.text = "Local Atual: " + passageiro.enderecoAtualUsuario + " - " + passageiro.bairroAtualUsuario + " - " + passageiro.cidadeAtualUsuario
                holder.destino.text = "Destino: " + destino!!.rua + " - " + destino.bairro + " - " + destino.cidade
                holder.distancia.text = "Distância aproximada até o passageiro: $distanciaFormatada"
            }
        } catch (e: Exception) {
            Log.d("ERRO!", "onKeyEntered: Algum erro interno occoreu." + e.message)
        }
    }

    override fun getItemCount(): Int = requisicoes.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.textRequisicaoNome)
        val distancia: TextView = itemView.findViewById(R.id.textRequisicaoDistancia)
        val endereco: TextView = itemView.findViewById(R.id.textRequisicaoEndereco)
        val destino: TextView = itemView.findViewById(R.id.textRequisicaoDestino)
    }
}
