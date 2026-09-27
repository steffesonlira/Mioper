package com.cursoandroid.mioper.firebase

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.cursoandroid.mioper.model.UserProfile
import com.cursoandroid.mioper.view.Principal
import com.cursoandroid.mioper.view.Requisicoes
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object UsuarioFirebase {

    fun getUsuarioAtual(): FirebaseUser? {
        return ConfiguracaoFirebase.getFirebaseAutenticacao().currentUser
    }

    fun getDadosUsuarioLogado(): UserProfile {
        val usuario = UserProfile()
        getUsuarioAtual()?.let { firebaseUser ->
            usuario.id = firebaseUser.uid
            usuario.email = firebaseUser.email
            usuario.name = firebaseUser.displayName
        }
        return usuario
    }

    fun atualizarNomeUsuario(nome: String): Boolean {
        return try {
            val profile = UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build()
            getUsuarioAtual()!!.updateProfile(profile).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("Perfil", "Erro ao atualizar nome de perfil.")
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun redirecionaUsuarioLogado(activity: Activity) {
        if (getUsuarioAtual() == null) return

        Log.d("resultado", "onDataChange: " + getIdentificadorUsuario())
        val usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
            .child("Users")
            .child(getIdentificadorUsuario())
        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("resultado", "onDataChange: $dataSnapshot")

                    val usuario = dataSnapshot.getValue(UserProfile::class.java)!!
                    if (usuario.tipouser == "M") {
                        activity.startActivity(Intent(activity, Requisicoes::class.java))
                    } else {
                        val i = Intent(activity, Principal::class.java)
                        i.putExtra("name", usuario.name)
                        activity.startActivity(i)
                    }
                } else {
                    alerta(activity)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    // Conta existe no Auth, mas os dados do usuário foram apagados do Database
    fun alerta(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Aviso!")
            .setMessage("Erro ao efetuar o login. (User information was deleted on database). Entre em contato com o suporte. (mioper.suporte@outlook.com)")
            .setPositiveButton("Ok") { _, _ -> }
            .create()
            .show()
    }

    fun atualizarDadosLocalizacao(lat: Double, lon: Double) {
        val localUsuario = ConfiguracaoFirebase.getFirebaseDatabase().child("local_usuario")
        val geoFire = GeoFire(localUsuario)

        val usuarioLogado = getDadosUsuarioLogado()
        usuarioLogado.id?.let { id ->
            geoFire.setLocation(id, GeoLocation(lat, lon)) { _, error ->
                if (error != null) {
                    Log.d("Erro", "Erro ao salvar local!")
                }
            }
        }
    }

    fun getIdentificadorUsuario(): String {
        return getUsuarioAtual()!!.uid
    }
}
