package com.cursoandroid.mioper.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object ConfiguracaoFirebase {

    private var database: DatabaseReference? = null
    private var auth: FirebaseAuth? = null

    fun getFirebaseDatabase(): DatabaseReference {
        return database ?: FirebaseDatabase.getInstance().reference.also { database = it }
    }

    fun getFirebaseAutenticacao(): FirebaseAuth {
        return auth ?: FirebaseAuth.getInstance().also { auth = it }
    }
}
