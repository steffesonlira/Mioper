package com.cursoandroid.mioper.model

import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import java.io.Serializable

class UserProfile : Serializable {
    var id: String? = null
    var name: String? = null
    var adress: String? = null
    var email: String? = null
    var cpf: String? = null
    var mobile: String? = null
    var nascimento: String? = null
    var senha: String? = null
    var repitasenha: String? = null
    var tipouser: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var latitude_atual: String? = null
    var longitude_atual: String? = null
    var genero: String? = null
    var enderecoAtualUsuario: String? = null
    var cidadeAtualUsuario: String? = null
    var bairroAtualUsuario: String? = null

    constructor()

    constructor(
        id: String?,
        name: String?,
        adress: String?,
        email: String?,
        cpf: String?,
        mobile: String?,
        nascimento: String?,
        senha: String?,
        repitasenha: String?,
        tipouser: String?,
        latitude: String?,
        longitude: String?,
        genero: String?
    ) {
        this.id = id
        this.name = name
        this.adress = adress
        this.email = email
        this.cpf = cpf
        this.mobile = mobile
        this.nascimento = nascimento
        this.senha = senha
        this.repitasenha = repitasenha
        this.tipouser = tipouser
        this.latitude = latitude
        this.longitude = longitude
        this.genero = genero
    }

    fun salvar() {
        ConfiguracaoFirebase.getFirebaseDatabase().child("Users").child(id!!).setValue(this)
    }

    fun remover() {
        ConfiguracaoFirebase.getFirebaseDatabase().child("Users").child(id!!).removeValue()
    }
}
