package com.cursoandroid.mioper.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.adapter.AdapterHistoricoViagem
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase
import com.cursoandroid.mioper.model.HistoricoUsuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HistoricoViagens : AppCompatActivity() {

    var nomeUsuario1: String? = null
    var celularUsuario: String? = null
    var senhaUsuario: String? = null
    var repitasenha: String? = null
    var emailUsuario: String? = null
    var enderecoUsuario: String? = null
    var nascimentoUsuario: String? = null
    var cpfUsuario: String? = null
    var generoUsuario: String? = null
    lateinit var historicoUser: TextView
    var activity: Principal? = null
    var tipoUsuario: String? = null
    private var listLocais: ListView? = null
    private lateinit var recyclerView: RecyclerView
    var array2 = ArrayList<HistoricoUsuario>()
    var userMailReplaced: String? = null
    var btnListar: Button? = null
    var dSnapshot: DataSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_historico_viagens)

        recyclerView = findViewById(R.id.recyclerView)
        historicoUser = findViewById(R.id.textView_viagem)

        historicoUser.visibility = View.INVISIBLE

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Histórico de Viagens"

        retornaHistorico()
    }

    fun retornaHistorico() {
        val user = UsuarioFirebase.getUsuarioAtual()!!
        userMailReplaced = user.email!!.replace('.', '-')

        Log.d("resultado", "onDataChange: " + UsuarioFirebase.getIdentificadorUsuario())
        val usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
            .child("historicoUsuario")
            .child(userMailReplaced!!)
        usuariosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("resultado", "onDataChange: $dataSnapshot")

                if (dataSnapshot.exists()) {
                    mostrarHistorico(dataSnapshot)
                } else {
                    historicoUser.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun mostrarHistorico(dataSnapshot: DataSnapshot) {
        for (ds in dataSnapshot.children) {
            val historico = ds.getValue(HistoricoUsuario::class.java)!!

            val uHistorico = HistoricoUsuario()
            uHistorico.bairro = historico.bairro
            uHistorico.cep = historico.cep
            uHistorico.cidade = historico.cidade
            uHistorico.data = historico.data
            uHistorico.rua = historico.rua
            uHistorico.latitude = historico.latitude
            uHistorico.longitude = historico.longitude
            uHistorico.numero = historico.numero

            array2.add(uHistorico)

            enviarDadosLista(array2)
        }
    }

    fun enviarDadosLista(array2: ArrayList<HistoricoUsuario>) {
        val adapter = AdapterHistoricoViagem(array2)

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)
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

    companion object {
        var adapter: ArrayAdapter<String>? = null
        var array = ArrayList<String>()
    }
}
