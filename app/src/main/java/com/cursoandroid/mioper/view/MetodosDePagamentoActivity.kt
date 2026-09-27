package com.cursoandroid.mioper.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.adapter.AdapterMetodosPagamentos
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MetodosDePagamentoActivity : AppCompatActivity() {
    lateinit var botaoCadastrarCartao: Button
    var userMailReplaced: String? = null

    private val metodosOk = ArrayList<String>()

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metodos_de_pagamento)
        resgatarMetodosPagamentos()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Métodos de Pagamento"

        botaoCadastrarCartao = findViewById(R.id.adcionarCartaoID)
        botaoCadastrarCartao.setOnClickListener {
            adicionarCartao()
        }
    }

    fun adicionarCartao() {
        startActivity(Intent(this, CadastrarCartaoActivity::class.java))
        finish()
    }

    fun resgatarMetodosPagamentos() {
        val user = UsuarioFirebase.getUsuarioAtual()!!
        userMailReplaced = user.email!!.replace('.', '-')

        Log.d("resultado", "onDataChange: " + UsuarioFirebase.getIdentificadorUsuario())
        val usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
            .child("CartoesPagamentos").child(userMailReplaced!!)

        usuariosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    metodosOk.add(ds.value!!.toString())
                    IniciarRecyclerView(metodosOk)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun IniciarRecyclerView(metodos: ArrayList<String>) {
        val adapter = AdapterMetodosPagamentos(metodos)

        val recyclerView = findViewById<RecyclerView>(R.id.listaMetodosPagamentosID)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}
