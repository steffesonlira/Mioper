package com.cursoandroid.mioper;


import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.nome.setText(passageiro.getName());
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
                holder.distancia.setText(distanciaFormatada + "- aproximadamente");

            }
        }catch (Exception e){
            Log.d("ERRO!", "onKeyEntered: Algum erro interno occoreu."+e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return requisicoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome, distancia;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textRequisicaoNome);
            distancia = itemView.findViewById(R.id.textRequisicaoDistancia);

        }
    }
}
