package com.cursoandroid.mioper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterMetodosPagamentos extends RecyclerView.Adapter<AdapterMetodosPagamentos.ViewHolder>{

    private static final String TAg = "RecyclerViewAdapter";

    private ArrayList<String> tipoPagamentoOK;

    public AdapterMetodosPagamentos(ArrayList tipoPag) {
        this.tipoPagamentoOK = tipoPag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_lista_pagamentos,
                viewGroup,false);
        ViewHolder hold = new ViewHolder(view);
        return hold;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.metodoPagamento.setText(tipoPagamentoOK.get(i));

    }

    @Override
    public int getItemCount() {
        return tipoPagamentoOK.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView metodoPagamento;
        public Button deletarNumero ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            metodoPagamento = itemView.findViewById(R.id.listaPagamentosID);

            deletarNumero = itemView.findViewById(R.id.removerCartaoID);
            deletarNumero.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tipoPagamentoOK != null){
                        tipoPagamentoOK.remove(this);
                    }
                }
            });

        }
    }
}
