package com.cursoandroid.mioper.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cursoandroid.mioper.model.Destino;
import com.cursoandroid.mioper.model.Local;
import com.cursoandroid.mioper.R;
import com.cursoandroid.mioper.model.Requisicao;
import com.cursoandroid.mioper.model.UserProfile;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    private List<Requisicao> requisicoes;
    private Context context;
    private UserProfile motorista;

    public RequisicoesAdapter(List<Requisicao> requisicoes, Context context, UserProfile motorista) {
        this.requisicoes = requisicoes;
        this.context = context;
        this.motorista = motorista;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_requisicoes, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Requisicao requisicao = requisicoes.get(position);
        UserProfile passageiro = requisicao.getPassageiro();
        Destino destino = requisicao.getDestino();

        holder.nome.setText("Passageiro: " + passageiro.getName());
        try {
            if (motorista != null) {

                LatLng localPassageiro = new LatLng(
                        Double.parseDouble(passageiro.getLatitude()),
                        Double.parseDouble(passageiro.getLongitude())
                );

                LatLng localMotorista = new LatLng(
                        Double.parseDouble(motorista.getLatitude()),
                        Double.parseDouble(motorista.getLongitude())
                );

                float distancia = Local.calcularDistancia(localPassageiro, localMotorista);
                String distanciaFormatada = Local.formatarDistancia(distancia);
                holder.endereco.setText("Local Atual: " + passageiro.enderecoAtualUsuario + " - " + passageiro.bairroAtualUsuario + " - " + passageiro.cidadeAtualUsuario);
                holder.destino.setText("Destino: " + destino.getRua() + " - " + destino.getBairro() + " - " + destino.getCidade());
                holder.distancia.setText("Distância aproximada até o passageiro: " + distanciaFormatada);

            }
        } catch (Exception e) {
            Log.d("ERRO!", "onKeyEntered: Algum erro interno occoreu." + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return requisicoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome, distancia, endereco, destino;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textRequisicaoNome);
            distancia = itemView.findViewById(R.id.textRequisicaoDistancia);
            endereco = itemView.findViewById(R.id.textRequisicaoEndereco);
            destino = itemView.findViewById(R.id.textRequisicaoDestino);

        }
    }
}
