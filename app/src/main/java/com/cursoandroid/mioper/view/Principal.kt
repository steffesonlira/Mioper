package com.cursoandroid.mioper.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.ui.AppBarConfiguration
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase.getIdentificadorUsuario
import com.cursoandroid.mioper.firebase.UsuarioFirebase.getUsuarioAtual
import com.cursoandroid.mioper.model.UserProfile
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class Principal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    lateinit var drawer: DrawerLayout
    lateinit var nomeUsuario: TextView
    var boasVindas_: String? = null
    private var gpsStatus = false
    lateinit var viewLayout: View
    lateinit var navigationView: NavigationView
    private lateinit var mAppBarConfiguration: AppBarConfiguration

    override fun onResume() {
        super.onResume()

        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // Recarrega os dados do usuário ao voltar da tela Meus Dados, que pode tê-los alterado
        if (verificaRetorno) {
            carregarDadosUsuario()
            verificaRetorno = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gpsStatus) {
            alertaGpsDesligado()
        } else {
            viewLayout = layoutInflater.inflate(R.layout.customtoast, findViewById<ViewGroup>(R.id.custom_layout))
            val toastCustom = Toast.makeText(this@Principal, "", Toast.LENGTH_LONG)
            toastCustom.setGravity(Gravity.CENTER, 0, 0)
            toastCustom.view = viewLayout
            toastCustom.show()
        }

        nomeUsuario = findViewById(R.id.nomeUsuário)
        intent.extras?.let { dados ->
            usuario = dados.getString("name")
        }

        val timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (timeOfDay in 0..11) {
            boasVindas_ = "Bom Dia"
        } else if (timeOfDay in 12..17) {
            boasVindas_ = "Boa Tarde"
        } else if (timeOfDay in 18..24) {
            boasVindas_ = "Boa Noite"
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()

        val headerView = navigationView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.nomeUsuário).text = usuario
        headerView.findViewById<TextView>(R.id.txtboasVindas).text = boasVindas_

        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.sair, R.id.nav_dados, R.id.nav_pagamento, R.id.nav_historico,
            R.id.nav_indicacao, R.id.nav_suporte, R.id.nav_sobre, R.id.nav_sair
        )
            .setDrawerLayout(drawer)
            .build()

        nomeUsuario = findViewById(R.id.nomeUsuário)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        this.navigationView = findViewById(R.id.nav_view)
        this.navigationView.setNavigationItemSelectedListener(this)
        this.navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        this.navigationView.setNavigationItemSelectedListener(this)
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().commitNowAllowingStateLoss()

        carregarDadosUsuario()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sair -> {
                if (!gpsStatus) {
                    alertaGpsDesligado()
                } else {
                    startActivity(Intent(this@Principal, Passageiro::class.java))
                }
            }
            R.id.nav_dados -> {
                if (isOnline(this)) {
                    val i = Intent(this@Principal, MeusDados::class.java)
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
                } else {
                    Toast.makeText(
                        this,
                        "Informações indisponíveis. Verifique sua conexão com a internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.nav_pagamento -> startActivity(Intent(this@Principal, MetodosDePagamentoActivity::class.java))
            R.id.nav_historico -> {
                if (isOnline(this)) {
                    startActivity(Intent(this@Principal, HistoricoViagens::class.java))
                } else {
                    Toast.makeText(
                        this,
                        "Informações indisponíveis. Verifique sua conexão com a internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.nav_indicacao -> startActivity(Intent(this@Principal, IndiqueGanhe::class.java))
            R.id.nav_suporte -> startActivity(Intent(this@Principal, SuporteUsuario::class.java))
            R.id.nav_sobre -> startActivity(Intent(this@Principal, Sobre::class.java))
            R.id.nav_sair -> Sair()
        }

        findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        return true
    }

    fun alertaGpsDesligado() {
        val builder = AlertDialog.Builder(this@Principal, R.style.AlertDialogTheme)
        val view2 = layoutInflater.inflate(
            R.layout.layout_success_dialog,
            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccess)
        )
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.success_title_sair_gps_ok)
        view2.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc_sair_gps_ok)
        view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
        view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
        view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

        val alertDialog = builder.create()

        view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
            alertDialog.dismiss()
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
        }

        view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()
    }

    fun Sair() {
        val builder = AlertDialog.Builder(this@Principal, R.style.AlertDialogTheme)
        val view2 = layoutInflater.inflate(
            R.layout.layout_success_dialog,
            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccess)
        )
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.warning_title_sair_ok)
        view2.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc_sair_ok)
        view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
        view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
        view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

        val alertDialog = builder.create()

        view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
            alertDialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, Login::class.java))
            finish()
        }

        view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()
    }

    override fun onBackPressed() {
    }

    private fun carregarDadosUsuario() {
        if (getUsuarioAtual() == null) return

        Log.d("resultado", "onDataChange: " + getIdentificadorUsuario())
        val usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
            .child("Users")
            .child(getIdentificadorUsuario())
        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("resultado", "onDataChange: $dataSnapshot")
                val usuario = dataSnapshot.getValue(UserProfile::class.java)!!
                nomeUsuario1 = usuario.name
                celularUsuario = usuario.mobile
                senhaUsuario = usuario.senha
                repitasenha = usuario.repitasenha
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

    companion object {
        var verificaRetorno = false
        private var nomeUsuario1: String? = null
        private var celularUsuario: String? = null
        private var senhaUsuario: String? = null
        private var repitasenha: String? = null
        private var emailUsuario: String? = null
        private var enderecoUsuario: String? = null
        private var nascimentoUsuario: String? = null
        private var cpfUsuario: String? = null
        private var generoUsuario: String? = null
        private var tipoUsuario: String? = null
        private var usuario: String? = null
        private const val TAG = "Principal"

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }
}
