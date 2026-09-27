package com.cursoandroid.mioper.model

import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase

class Requisicao {
    var id: String? = null
    var status: String? = null
    var passageiro: UserProfile? = null
    var motorista: UserProfile? = null
    var destino: Destino? = null

    fun salvar() {
        val requisicoes = ConfiguracaoFirebase.getFirebaseDatabase().child("requisicoes")

        id = requisicoes.push().key
        requisicoes.child(id!!).setValue(this)
    }

    fun salvarHistorico(destino: Destino?) {
        val historico = ConfiguracaoFirebase.getFirebaseDatabase()
            .child("historicoUsuario")
            .child(emailUsuarioComoChave())

        idRequisicaoHistorico = historico.push().key
        id = idRequisicaoHistorico
        historico.child(id!!).setValue(destino)
    }

    fun excluirHistorico() {
        ConfiguracaoFirebase.getFirebaseDatabase()
            .child("historicoUsuario")
            .child(emailUsuarioComoChave())
            .child(idRequisicaoHistorico!!)
            .removeValue()
    }

    fun atualizar() {
        val objeto = mapOf<String, Any?>(
            "motorista" to motorista,
            "status" to status
        )
        requisicaoRef().updateChildren(objeto)
    }

    fun atualizarStatus() {
        requisicaoRef().updateChildren(mapOf<String, Any?>("status" to status))
    }

    fun atualizarLocalizacaoMotorista() {
        val objeto = mapOf<String, Any?>(
            "latitude" to motorista?.latitude,
            "longitude" to motorista?.longitude
        )
        requisicaoRef().child("motorista").updateChildren(objeto)
    }

    fun atualizarLocalizacaoAtualPassageiro() {
        val objeto = mapOf<String, Any?>(
            "latitude_atual" to motorista?.latitude,
            "longitude_atual" to motorista?.longitude
        )
        requisicaoRef().child("passageiro").updateChildren(objeto)
    }

    private fun requisicaoRef() =
        ConfiguracaoFirebase.getFirebaseDatabase().child("requisicoes").child(id!!)

    // O Firebase não aceita "." em chaves de nó
    private fun emailUsuarioComoChave() =
        UsuarioFirebase.getUsuarioAtual()!!.email!!.replace('.', '-')

    companion object {
        const val STATUS_AGUARDANDO = "aguardando"
        const val STATUS_A_CAMINHO = "acaminho"
        const val STATUS_VIAGEM = "viagem"
        const val STATUS_FINALIZADA = "finalizada"
        const val STATUS_ENCERRADA = "encerrada"
        const val STATUS_CANCELADA = "cancelada"

        private var idRequisicaoHistorico: String? = null
    }
}
