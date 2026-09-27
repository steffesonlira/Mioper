package com.cursoandroid.mioper.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase
import com.cursoandroid.mioper.model.Destino
import com.cursoandroid.mioper.model.Local
import com.cursoandroid.mioper.model.Requisicao
import com.cursoandroid.mioper.model.UserProfile
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat

class Corrida : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var buttonAceitarCorrida: Button
    private lateinit var fabRota: FloatingActionButton

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var localMotorista: LatLng? = null
    private var localPassageiro: LatLng? = null
    private var statusDaViagem: String? = null
    private var motorista: UserProfile? = null
    private var passageiro: UserProfile? = null
    private var idRequisicao: String? = null
    private var requisicao: Requisicao? = null
    private lateinit var firebaseRef: DatabaseReference
    private var marcadorMotorista: Marker? = null
    private var marcadorPassageiro: Marker? = null
    private var marcadorDestino: Marker? = null
    private var statusRequisicao: String? = null
    private var requisicaoAtiva = false
    private var destino: Destino? = null
    private var tipoRequisicao: String? = null
    private var distancia = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_corrida)

        inicializarComponentes()

        val extras = intent.extras!!
        if (extras.containsKey("idRequisicao") && extras.containsKey("motorista")) {
            motorista = extras.getSerializable("motorista") as UserProfile
            localMotorista = LatLng(
                motorista!!.latitude!!.toDouble(),
                motorista!!.longitude!!.toDouble()
            )
            idRequisicao = extras.getString("idRequisicao")
            requisicaoAtiva = extras.getBoolean("requisicaoAtiva")

            verificaStatusRequisicao()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        recuperarLocalizacaoUsuario()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (requisicaoAtiva) {
            Toast.makeText(
                this@Corrida,
                "Necessário encerrar a requisição atual!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            startActivity(Intent(this@Corrida, Requisicoes::class.java))
        }

        if (!statusRequisicao.isNullOrEmpty()) {
            requisicao!!.status = Requisicao.STATUS_ENCERRADA
            requisicao!!.atualizarStatus()
        }

        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> startActivity(Intent(this, Requisicoes::class.java))

            R.id.menuEncerrarViagem -> {
                if (buttonAceitarCorrida.isEnabled) {
                    Toast.makeText(this, "A viagem ainda não foi iniciada!", Toast.LENGTH_SHORT).show()
                } else if (statusDaViagem == "acaminho") {
                    Toast.makeText(this, "O passageiro ainda não foi embarcado!", Toast.LENGTH_SHORT).show()
                } else {
                    verificaEncerramento = true
                    statusDaViagem = "finalizada"
                    requisicao!!.atualizarLocalizacaoAtualPassageiro()
                    requisicao!!.status = Requisicao.STATUS_FINALIZADA
                    requisicao!!.atualizarStatus()
                }
            }
        }
        return true
    }

    private fun verificaStatusRequisicao() {
        val requisicoes = firebaseRef.child("requisicoes").child(idRequisicao!!)
        requisicoes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    requisicao = dataSnapshot.getValue(Requisicao::class.java)
                    tipoRequisicao = requisicao!!.status
                    requisicao?.let { requisicao ->
                        passageiro = requisicao.passageiro
                        localPassageiro = LatLng(
                            passageiro!!.latitude!!.toDouble(),
                            passageiro!!.longitude!!.toDouble()
                        )
                        statusRequisicao = requisicao.status
                        destino = requisicao.destino
                        alteraInterfaceStatusRequisicao(statusRequisicao)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun alteraInterfaceStatusRequisicao(status: String?) {
        when (status) {
            Requisicao.STATUS_AGUARDANDO -> requisicaoAguardando()
            Requisicao.STATUS_A_CAMINHO -> requisicaoACaminho()
            Requisicao.STATUS_VIAGEM -> requisicaoViagem()
            Requisicao.STATUS_FINALIZADA -> requisicaoFinalizada()
            Requisicao.STATUS_CANCELADA -> requisicaoCancelada()
        }
    }

    private fun requisicaoCancelada() {
    }

    @SuppressLint("RestrictedApi")
    private fun requisicaoFinalizada() {
        locationManager.removeUpdates(locationListener)

        fabRota.visibility = View.GONE
        requisicaoAtiva = false

        marcadorMotorista?.remove()
        marcadorDestino?.remove()

        val localDestino = LatLng(
            destino!!.latitude!!.toDouble(),
            destino!!.longitude!!.toDouble()
        )
        adicionaMarcadorDestino(localDestino, "Destino")
        centralizarMarcador(localDestino)

        // Encerrada antes pelo motorista: cobra até onde ele está; finalizada pelo sistema: até o destino
        distancia = if (verificaEncerramento) {
            println("LOCAL MOTORISTA" + localPassageiro + localMotorista)
            Local.calcularDistancia(localPassageiro!!, localMotorista!!).toDouble()
        } else {
            Local.calcularDistancia(localPassageiro!!, localDestino).toDouble()
        }
        val valor = distancia * 8
        val resultado = DecimalFormat("0.00").format(valor)

        buttonAceitarCorrida.isEnabled = false
        buttonAceitarCorrida.text = "Corrida finalizada - R$ $resultado"

        val builder = AlertDialog.Builder(this@Corrida, R.style.AlertDialogTheme)
        val view2 = LayoutInflater.from(this@Corrida).inflate(
            R.layout.layout_successok_dialog,
            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccessOk)
        )
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccessOk).text = resources.getString(R.string.success_title_enc_viagem)
        view2.findViewById<TextView>(R.id.textMessageSuccessOk).text = "O valor a ser recebido é de R$$resultado"
        view2.findViewById<Button>(R.id.buttonSuccessOk).text = "Encerrar Viagem"
        view2.findViewById<ImageView>(R.id.imageIconSuccessOk).setImageResource(R.drawable.logo)

        try {
            val alertDialog = builder.create()

            view2.findViewById<View>(R.id.buttonSuccessOk).setOnClickListener {
                requisicao!!.status = Requisicao.STATUS_ENCERRADA
                requisicao!!.atualizarStatus()
                verificaEncerramento = false
                alertDialog.dismiss()

                finish()
            }

            alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
            alertDialog.show()
        } catch (e: Exception) {
            println("ERRO" + e.message)
        }
    }

    private fun centralizarMarcador(local: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 18f))
    }

    private fun requisicaoAguardando() {
        buttonAceitarCorrida.text = "Aceitar corrida"

        adicionaMarcadorMotorista(localMotorista!!, motorista!!.name)
        centralizarMarcador(localMotorista!!)
    }

    @SuppressLint("RestrictedApi")
    private fun requisicaoACaminho() {
        // Só permite encerrar a viagem antecipadamente quando o status for "viagem"
        statusDaViagem = "acaminho"

        buttonAceitarCorrida.text = "A caminho do passageiro"
        buttonAceitarCorrida.isEnabled = false
        fabRota.visibility = View.VISIBLE

        adicionaMarcadorMotorista(localMotorista!!, motorista!!.name)
        adicionaMarcadorPassageiro(localPassageiro!!, passageiro!!.name)
        centralizarDoisMarcadores(marcadorMotorista!!, marcadorPassageiro!!)

        iniciarMonitoramento(motorista!!, localPassageiro!!, Requisicao.STATUS_VIAGEM)
    }

    @SuppressLint("RestrictedApi")
    private fun requisicaoViagem() {
        statusDaViagem = "viagem"

        fabRota.visibility = View.VISIBLE
        buttonAceitarCorrida.text = "A caminho do destino"

        adicionaMarcadorMotorista(localMotorista!!, motorista!!.name)

        val localDestino = LatLng(
            destino!!.latitude!!.toDouble(),
            destino!!.longitude!!.toDouble()
        )
        adicionaMarcadorDestino(localDestino, "Destino")
        centralizarDoisMarcadores(marcadorMotorista!!, marcadorDestino!!)

        iniciarMonitoramento(motorista!!, localDestino, Requisicao.STATUS_FINALIZADA)
    }

    private fun iniciarMonitoramento(uOrigem: UserProfile, localDestino: LatLng, status: String) {
        if (status == "finalizada") return

        val localUsuario = ConfiguracaoFirebase.getFirebaseDatabase().child("local_usuario")
        val geoFire = GeoFire(localUsuario)

        val circulo = mMap.addCircle(
            CircleOptions()
                .center(localDestino)
                .radius(30.0)
                .fillColor(Color.argb(90, 255, 153, 0))
                .strokeColor(Color.argb(190, 255, 152, 0))
        )

        // Raio em km: 0.03 = 30 metros, igual ao círculo desenhado
        val geoQuery = geoFire.queryAtLocation(
            GeoLocation(localDestino.latitude, localDestino.longitude),
            0.03
        )
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {
                if (key == uOrigem.id) {
                    Log.d("onKeyEntered", "onKeyEntered: motorista está dentro da área!")

                    requisicao!!.status = status
                    requisicao!!.atualizarStatus()

                    geoQuery.removeAllListeners()
                    circulo.remove()
                }
            }

            override fun onKeyExited(key: String) {
            }

            override fun onKeyMoved(key: String, location: GeoLocation) {
            }

            override fun onGeoQueryReady() {
            }

            override fun onGeoQueryError(error: DatabaseError) {
            }
        })
    }

    private fun centralizarDoisMarcadores(marcador1: Marker, marcador2: Marker) {
        val bounds = LatLngBounds.Builder()
            .include(marcador1.position)
            .include(marcador2.position)
            .build()

        val largura = resources.displayMetrics.widthPixels
        val altura = resources.displayMetrics.heightPixels
        val espacoInterno = (largura * 0.30).toInt()

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, largura, altura, espacoInterno))
    }

    private fun adicionaMarcadorMotorista(localizacao: LatLng, titulo: String?) {
        marcadorMotorista?.remove()

        marcadorMotorista = mMap.addMarker(
            MarkerOptions()
                .position(localizacao)
                .title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.van3))
        )
    }

    private fun adicionaMarcadorPassageiro(localizacao: LatLng, titulo: String?) {
        marcadorPassageiro?.remove()

        marcadorPassageiro = mMap.addMarker(
            MarkerOptions()
                .position(localizacao)
                .title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.passageiro))
        )
    }

    private fun adicionaMarcadorDestino(localizacao: LatLng, titulo: String?) {
        marcadorPassageiro?.remove()
        marcadorDestino?.remove()

        marcadorDestino = mMap.addMarker(
            MarkerOptions()
                .position(localizacao)
                .title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.chegada))
        )
    }

    private fun recuperarLocalizacaoUsuario() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                try {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    localMotorista = LatLng(latitude, longitude)

                    UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude)

                    motorista!!.latitude = latitude.toString()
                    motorista!!.longitude = longitude.toString()
                    requisicao!!.motorista = motorista
                    requisicao!!.atualizarLocalizacaoMotorista()

                    alteraInterfaceStatusRequisicao(statusRequisicao)
                } catch (e: Exception) {
                    println("OCORREU ALGUM ERRO INTERNO" + e.message)
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
                10000,
                10f,
                locationListener
            )
        }
    }

    private fun inicializarComponentes() {
        buttonAceitarCorrida = findViewById(R.id.buttonAceitarCorrida)

        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fabRota = findViewById(R.id.fabRota)
        fabRota.setOnClickListener {
            val status = statusRequisicao
            if (!status.isNullOrEmpty()) {
                var lat: String? = ""
                var lon: String? = ""

                when (status) {
                    Requisicao.STATUS_A_CAMINHO -> {
                        lat = localPassageiro!!.latitude.toString()
                        lon = localPassageiro!!.longitude.toString()
                    }
                    Requisicao.STATUS_VIAGEM -> {
                        lat = destino!!.latitude
                        lon = destino!!.longitude
                    }
                }

                val uri = Uri.parse("google.navigation:q=$lat,$lon&mode=d")
                val i = Intent(Intent.ACTION_VIEW, uri)
                i.setPackage("com.google.android.apps.maps")
                startActivity(i)
            }
        }
    }

    fun aceitarCorrida(view: View) {
        verificaStatusRequisicao()
        if (tipoRequisicao != "cancelada") {
            requisicao = Requisicao().apply {
                id = idRequisicao
                motorista = this@Corrida.motorista
                status = Requisicao.STATUS_A_CAMINHO
            }
            requisicao!!.atualizar()
        } else {
            val builder = AlertDialog.Builder(this@Corrida, R.style.AlertDialogTheme)
            val view2 = LayoutInflater.from(this@Corrida).inflate(
                R.layout.layout_successok_dialog,
                findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccessOk)
            )
            builder.setView(view2)
            view2.findViewById<TextView>(R.id.textTitleSuccessOk).text = resources.getString(R.string.warning_title_ok)
            view2.findViewById<TextView>(R.id.textMessageSuccessOk).text = resources.getString(R.string.text_desc_ok_viagem_cancelada)
            view2.findViewById<Button>(R.id.buttonSuccessOk).text = resources.getString(R.string.confirmar)
            view2.findViewById<ImageView>(R.id.imageIconSuccessOk).setImageResource(R.drawable.logo)

            val alertDialog = builder.create()
            view2.findViewById<View>(R.id.buttonSuccessOk).setOnClickListener {
                alertDialog.dismiss()
                finish()
            }
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
            alertDialog.show()
        }
    }

    companion object {
        private var verificaEncerramento = false
    }
}
