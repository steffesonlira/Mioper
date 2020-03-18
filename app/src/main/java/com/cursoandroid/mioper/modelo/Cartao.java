package com.cursoandroid.mioper.modelo;

import com.cursoandroid.mioper.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Cartao {
    private String numeroCartao;


    public Cartao(){

    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference numeroCartao = firebaseRef.child("NumeroCartao");

        String nCartao = numeroCartao.push().getKey();
        setNumeroCartao( nCartao );

        numeroCartao.child( getNumeroCartao() ).setValue(this);
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }


}
