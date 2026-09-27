package com.cursoandroid.mioper.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cursoandroid.mioper.R
import com.google.firebase.auth.FirebaseAuth

class Sobre : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var btn_manifesto: Button
    private lateinit var btn_design: Button
    private lateinit var btn_historia: Button
    private lateinit var btn_missao: Button
    private lateinit var btn_visao: Button
    private lateinit var btn_valores: Button
    private lateinit var viewLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre)
        mAuth = FirebaseAuth.getInstance()
        btn_manifesto = findViewById(R.id.btnManifesto)
        btn_design = findViewById(R.id.btnDesignThinking)
        btn_historia = findViewById(R.id.btnHistoria)
        btn_missao = findViewById(R.id.btnMissao)
        btn_visao = findViewById(R.id.btnVisao)
        btn_valores = findViewById(R.id.btnValores)
        viewLayout = layoutInflater.inflate(R.layout.customtoast, findViewById<ViewGroup>(R.id.custom_layout))

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Sobre o Mioper"

        btn_manifesto.setOnClickListener { startActivity(Intent(this, Onboarding::class.java)) }
        btn_design.setOnClickListener { startActivity(Intent(this, DesignThinking::class.java)) }
        btn_historia.setOnClickListener { startActivity(Intent(this, Historia::class.java)) }
        btn_missao.setOnClickListener { startActivity(Intent(this, Missao::class.java)) }
        btn_visao.setOnClickListener { startActivity(Intent(this, Visao::class.java)) }
        btn_valores.setOnClickListener { startActivity(Intent(this, Valores::class.java)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
}
