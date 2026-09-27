package com.cursoandroid.mioper.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.adapter.RecyclerItemClickListener
import com.cursoandroid.mioper.adapter.RequisicoesAdapter
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase
import com.cursoandroid.mioper.model.Requisicao
import com.cursoandroid.mioper.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class Requisicoes : AppCompatActivity() {

    private lateinit var recyclerRequisicoes: RecyclerView
    private lateinit var textResultado: TextView

    private lateinit var autenticacao: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference
    private val listaRequisicoes: MutableList<Requisicao> = ArrayList()
    private lateinit var adapter: RequisicoesAdapter
    private var motorista: UserProfile? = null
    private var _dataSnapshot: DataSnapshot? = null

    private var nomeUsuario1: String? = null
    private var celularUsuario: String? = null
    private var senhaUsuario: String? = null
    private var emailUsuario: String? = null
    private var enderecoUsuario: String? = null
    private var nascimentoUsuario: String? = null
    private var cpfUsuario: String? = null
    private var generoUsuario: String? = null
    private var tipoUsuario: String? = null

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requisicoes)

        // Carrega os dados do motorista para repassar à tela de Meus Dados
        if (UsuarioFirebase.getUsuarioAtual() != null) {
            Log.d("resultado", "onDataChange: " + UsuarioFirebase.getIdentificadorUsuario())
            val usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Users")
                .child(UsuarioFirebase.getIdentificadorUsuario())
            usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("resultado", "onDataChange: $dataSnapshot")
                    val usuario = dataSnapshot.getValue(UserProfile::class.java)!!

                    _dataSnapshot = dataSnapshot

                    nomeUsuario1 = usuario.name
                    celularUsuario = usuario.mobile
                    senhaUsuario = usuario.senha
                    emailUsuario = usuario.email
                    enderecoUsuario = usuario.adress
                    nascimentoUsuario = usuario.nascimento
                    cpfUsuario = usuario.cpf
                    generoUsuario = usuario.genero
                    tipoUsuario = usuario.tipouser
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Requisições"

        inicializarComponentes()

        recuperarLocalizacaoUsuario()
    }

    override fun onStart() {
        super.onStart()

        verificaStatusRequisicao()
    }

    override fun onResume() {
        super.onResume()
        inicializarComponentes()
        recuperarLocalizacaoUsuario()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val builder2 = AlertDialog.Builder(this@Requisicoes, R.style.AlertDialogTheme)
                val view2 = LayoutInflater.from(this@Requisicoes).inflate(
                    R.layout.layout_success_dialog,
                    findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccess)
                )
                builder2.setView(view2)
                view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.warning_title_requisicao)
                view2.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc_requisicao)
                view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
                view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
                view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

                val alertDialog = builder2.create()

                view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
                    alertDialog.dismiss()
                    autenticacao.signOut()
                    val i = Intent(this@Requisicoes, Login::class.java)
                    finishAffinity()
                    startActivity(i)
                }

                view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
                alertDialog.show()
            }
            R.id.menuSair -> {
                val builder3 = AlertDialog.Builder(this@Requisicoes, R.style.AlertDialogTheme)
                val view3 = LayoutInflater.from(this@Requisicoes).inflate(
                    R.layout.layout_success_dialog,
                    findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccess)
                )
                builder3.setView(view3)
                view3.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.warning_title_requisicao)
                view3.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc_requisicao)
                view3.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
                view3.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
                view3.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

                val alertDialogSair = builder3.create()

                view3.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
                    alertDialogSair.dismiss()
                    autenticacao.signOut()
                    startActivity(Intent(this@Requisicoes, Login::class.java))
                    finishAffinity()
                }

                view3.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
                    alertDialogSair.dismiss()
                }
                alertDialogSair.window?.setBackgroundDrawable(ColorDrawable(0))
                alertDialogSair.show()
            }
            R.id.gerenciarDados -> {
                try {
                    if (!_dataSnapshot!!.exists()) {
                        val builder = AlertDialog.Builder(this@Requisicoes, R.style.AlertDialogTheme)
                        val view4 = LayoutInflater.from(this@Requisicoes).inflate(
                            R.layout.layout_successok_dialog,
                            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccessOk)
                        )
                        builder.setView(view4)
                        view4.findViewById<TextView>(R.id.textTitleSuccessOk).text = resources.getString(R.string.success_title_nao_motorista)
                        view4.findViewById<TextView>(R.id.textMessageSuccessOk).text = resources.getString(R.string.text_desc_nao_motorista)
                        view4.findViewById<Button>(R.id.buttonSuccessOk).text = resources.getString(R.string.confirmar)
                        view4.findViewById<ImageView>(R.id.imageIconSuccessOk).setImageResource(R.drawable.logo)

                        val alertDialog2 = builder.create()

                        view4.findViewById<View>(R.id.buttonSuccessOk).setOnClickListener {
                            alertDialog2.dismiss()
                        }
                        alertDialog2.window?.setBackgroundDrawable(ColorDrawable(0))
                        alertDialog2.show()
                    } else {
                        val i = Intent(this@Requisicoes, MeusDados::class.java)
                        i.putExtra("name", nomeUsuario1)
                        i.putExtra("mobile", celularUsuario)
                        i.putExtra("senha", senhaUsuario)
                        i.putExtra("email", emailUsuario)
                        i.putExtra("adress", enderecoUsuario)
                        i.putExtra("nascimento", nascimentoUsuario)
                        i.putExtra("cpf", cpfUsuario)
                        i.putExtra("genero", generoUsuario)
                        i.putExtra("tipouser", tipoUsuario)

                        startActivity(i)
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@Requisicoes,
                        "Por Favor realize a operação novamente!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return true
    }

    override fun onBackPressed() {
        val builder2 = AlertDialog.Builder(this@Requisicoes, R.style.AlertDialogTheme)
        val view2 = LayoutInflater.from(this@Requisicoes).inflate(
            R.layout.layout_success_dialog,
            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccess)
        )
        builder2.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.warning_title_requisicao)
        view2.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc_requisicao)
        view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
        view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
        view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

        val alertDialog = builder2.create()

        view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
            alertDialog.dismiss()
            autenticacao.signOut()
            val i = Intent(this@Requisicoes, Login::class.java)
            finishAffinity()
            startActivity(i)
        }

        view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()
    }

    private fun verificaStatusRequisicao() {
        val usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado()

        val requisicoesPesquisa = ConfiguracaoFirebase.getFirebaseDatabase()
            .child("requisicoes")
            .orderByChild("motorista/id")
            .equalTo(usuarioLogado.id)

        requisicoesPesquisa.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val requisicao = ds.getValue(Requisicao::class.java)!!

                    if (requisicao.status == Requisicao.STATUS_A_CAMINHO ||
                        requisicao.status == Requisicao.STATUS_VIAGEM ||
                        requisicao.status == Requisicao.STATUS_FINALIZADA
                    ) {
                        motorista = requisicao.motorista
                        abrirTelaCorrida(requisicao.id, motorista, true)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun recuperarLocalizacaoUsuario() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude.toString()
                val longitude = location.longitude.toString()

                UsuarioFirebase.atualizarDadosLocalizacao(location.latitude, location.longitude)

                if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                    motorista!!.latitude = latitude
                    motorista!!.longitude = longitude

                    adicionaEventoCliqueRecyclerView()
                    locationManager.removeUpdates(locationListener)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
        }
    }

    private fun abrirTelaCorrida(idRequisicao: String?, motorista: UserProfile?, requisicaoAtiva: Boolean) {
        val i = Intent(this@Requisicoes, Corrida::class.java)
        i.putExtra("idRequisicao", idRequisicao)
        i.putExtra("motorista", motorista)
        i.putExtra("requisicaoAtiva", requisicaoAtiva)
        startActivity(i)
    }

    private fun inicializarComponentes() {
        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes)
        textResultado = findViewById(R.id.textResultado)

        motorista = UsuarioFirebase.getDadosUsuarioLogado()
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()

        adapter = RequisicoesAdapter(listaRequisicoes, applicationContext, motorista!!)
        recyclerRequisicoes.layoutManager = LinearLayoutManager(applicationContext)
        recyclerRequisicoes.setHasFixedSize(true)
        recyclerRequisicoes.adapter = adapter

        recuperarRequisicoes()
    }

    private fun adicionaEventoCliqueRecyclerView() {
        recyclerRequisicoes.addOnItemTouchListener(
            RecyclerItemClickListener(
                applicationContext,
                recyclerRequisicoes,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val requisicao = listaRequisicoes[position]
                        abrirTelaCorrida(requisicao.id, motorista, false)
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                    }

                    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    }
                }
            )
        )
    }

    private fun recuperarRequisicoes() {
        val requisicaoPesquisa = firebaseRef.child("requisicoes")
            .orderByChild("status")
            .equalTo(Requisicao.STATUS_AGUARDANDO)

        requisicaoPesquisa.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.childrenCount > 0) {
                    textResultado.visibility = View.GONE
                    recyclerRequisicoes.visibility = View.VISIBLE
                } else {
                    textResultado.visibility = View.VISIBLE
                    recyclerRequisicoes.visibility = View.GONE
                }

                listaRequisicoes.clear()
                for (ds in dataSnapshot.children) {
                    ds.getValue(Requisicao::class.java)?.let { listaRequisicoes.add(it) }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}
