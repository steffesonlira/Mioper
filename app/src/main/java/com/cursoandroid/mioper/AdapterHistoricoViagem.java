package com.cursoandroid.mioper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterHistoricoViagem extends RecyclerView.Adapter<AdapterHistoricoViagem.MyViewHolder> {


    private ArrayList<HistoricoUsuario> listaUsuario;


    public AdapterHistoricoViagem(ArrayList lista) {
        this.listaUsuario = lista;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_lista, viewGroup, false);
        return new MyViewHolder(itemLista);
    }


    //COLOCA OS DADOS DO HISTORICO NO RECYCLERVIEW
    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        HistoricoUsuario historico = listaUsuario.get(i);

        myViewHolder.cidade.setText(historico.getCidade());
        myViewHolder.rua.setText(historico.getRua());
        myViewHolder.data.setText(historico.getData());
    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
    }

    //GUARDA OS DADOS ANTES DE SEREM EXIBIDOS NA TELA
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cidade;
        TextView rua;
        TextView data;

        public MyViewHolder(View itemView) {
            super(itemView);
            cidade = itemView.findViewById(R.id.textViewCidade);
            rua = itemView.findViewById(R.id.textViewRua);
            data = itemView.findViewById(R.id.textViewData);

        }
    }
}
