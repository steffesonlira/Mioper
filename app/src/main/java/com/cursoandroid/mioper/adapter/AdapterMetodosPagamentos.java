package com.cursoandroid.mioper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase;
import com.cursoandroid.mioper.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.cursoandroid.mioper.firebase.UsuarioFirebase.getUsuarioAtual;

public class AdapterMetodosPagamentos extends RecyclerView.Adapter<AdapterMetodosPagamentos.ViewHolder>{

    private static final String TAg = "RecyclerViewAdapter";
    private ArrayList<String> tipoPagamentoOK;
    public String userMailReplaced;


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

        viewHolder.deletarNumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser firebaseUser = getUsuarioAtual();
                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

                String userEmail = firebaseUser.getEmail();
                String userMailReplaced = userEmail.replace('.', '-');

                //CAMINHO DA REMOÇÃO DO FIREBASE
                DatabaseReference metodosPagamentos = firebaseRef.child("CartoesPagamentos")
                        .child(userMailReplaced)
                        .child(tipoPagamentoOK.get(i));

                metodosPagamentos.removeValue();

                //REMOÇÃO DO ITEM DA LISTA
                tipoPagamentoOK.clear();
//
            }
        });

    }

    @Override
    public int getItemCount() {
        return tipoPagamentoOK.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView metodoPagamento;
        Button deletarNumero ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            metodoPagamento = itemView.findViewById(R.id.listaPagamentosID);
            deletarNumero = itemView.findViewById(R.id.removerCartaoID);

        }
    }
}
