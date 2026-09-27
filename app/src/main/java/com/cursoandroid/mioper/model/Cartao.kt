package com.cursoandroid.mioper.model

import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase

class Cartao {

    var numeroCartao: String? = null

    fun salvar() {
        val numeroCartaoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("NumeroCartao")

        numeroCartao = numeroCartaoRef.push().key
        numeroCartaoRef.child(numeroCartao!!).setValue(this)
    }
}
