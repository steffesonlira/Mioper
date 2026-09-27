package com.cursoandroid.mioper.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cursoandroid.mioper.R
import com.google.firebase.auth.FirebaseAuth

class RequisitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requisition)
    }

    fun logOut(view: View) {
        FirebaseAuth.getInstance().signOut()
        finish()

        startActivity(Intent(applicationContext, Login::class.java))
    }
}
