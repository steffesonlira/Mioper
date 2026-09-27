package com.cursoandroid.mioper.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Passageiro : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var editDestino: EditText
    private lateinit var linearLayoutDestino: LinearLayout
    private lateinit var buttonChamarUber: Button
    private var distancia = 0.0
    private lateinit var mMap: GoogleMap
    private lateinit var autenticacao: FirebaseAuth
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var localPassageiro: LatLng? = null
    // true quando a requisição ainda está aguardando motorista e pode ser cancelada
    private var cancelarUber = false
    private lateinit var firebaseRef: DatabaseReference
    private var requisicao: Requisicao? = null
    private var tipoRequisicao: String? = null
    private var passageiro: UserProfile? = null
    private var statusRequisicao: String? = null
    private var destino: Destino? = null
    private var marcadorMotorista: Marker? = null
    private var marcadorPassageiro: Marker? = null
    private var marcadorDestino: Marker? = null
    private var motorista: UserProfile? = null
    private var localMotorista: LatLng? = null
    private var localMotoristaAtual: LatLng? = null
    private var _latitude = 0.0
    private var _longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passageiro)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Navegue com o Mioper"

        inicializarComponentes()

        verificaStatusRequisicao()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        recuperarLocalizacaoUsuario()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun verificaStatusRequisicao() {
        val usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado()
        val requisicaoPesquisa = firebaseRef.child("requisicoes")
            .orderByChild("passageiro/id")
            .equalTo(usuarioLogado.id)

        requisicaoPesquisa.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lista = dataSnapshot.children.map { it.getValue(Requisicao::class.java) }.reversed()

                if (lista.isNotEmpty()) {
                    val requisicaoAtual = lista[0]!!
                    requisicao = requisicaoAtual
                    tipoRequisicao = requisicaoAtual.status

                    if (requisicaoAtual.status != Requisicao.STATUS_ENCERRADA) {
                        val passageiroAtual = requisicaoAtual.passageiro!!
                        passageiro = passageiroAtual
                        localPassageiro = LatLng(
                            passageiroAtual.latitude!!.toDouble(),
                            passageiroAtual.longitude!!.toDouble()
                        )
                        statusRequisicao = requisicaoAtual.status
                        destino = requisicaoAtual.destino

                        // Preenchida quando o motorista encerra a viagem antes de chegar ao destino
                        val latitudeAtual = passageiroAtual.latitude_atual
                        val longitudeAtual = passageiroAtual.longitude_atual
                        if (latitudeAtual != null && longitudeAtual != null) {
                            localMotoristaAtual = LatLng(latitudeAtual.toDouble(), longitudeAtual.toDouble())
                        }

                        requisicaoAtual.motorista?.let { motoristaAtual ->
                            motorista = motoristaAtual
                            localMotorista = LatLng(
                                motoristaAtual.latitude!!.toDouble(),
                                motoristaAtual.longitude!!.toDouble()
                            )
                        }

                        alteraInterfaceStatusRequisicao(statusRequisicao)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun alteraInterfaceStatusRequisicao(status: String?) {
        if (!status.isNullOrEmpty()) {
            cancelarUber = false
            when (status) {
                Requisicao.STATUS_AGUARDANDO -> requisicaoAguardando()
                Requisicao.STATUS_A_CAMINHO -> requisicaoACaminho()
                Requisicao.STATUS_VIAGEM -> requisicaoViagem()
                Requisicao.STATUS_FINALIZADA -> requisicaoFinalizada()
                Requisicao.STATUS_CANCELADA -> requisicaoCancelada()
            }
        } else {
            adicionaMarcadorPassageiro(localPassageiro!!, "Seu local")
            centralizarMarcador(localPassageiro!!)
        }
    }

    private fun requisicaoCancelada() {
        linearLayoutDestino.visibility = View.VISIBLE
        buttonChamarUber.text = "Chamar Mioper"
        cancelarUber = false
    }

    private fun requisicaoAguardando() {
        linearLayoutDestino.visibility = View.GONE
        buttonChamarUber.text = "Cancelar Mioper"
        cancelarUber = true

        adicionaMarcadorPassageiro(localPassageiro!!, passageiro!!.name)
        centralizarMarcador(localPassageiro!!)
    }

    private fun requisicaoACaminho() {
        linearLayoutDestino.visibility = View.GONE
        buttonChamarUber.text = "Motorista a caminho"
        buttonChamarUber.isEnabled = false

        adicionaMarcadorPassageiro(localPassageiro!!, passageiro!!.name)
        adicionaMarcadorMotorista(localMotorista!!, motorista!!.name)

        centralizarDoisMarcadores(marcadorMotorista, marcadorPassageiro)
    }

    private fun requisicaoViagem() {
        linearLayoutDestino.visibility = View.GONE
        buttonChamarUber.text = "A caminho do destino"
        buttonChamarUber.isEnabled = false

        adicionaMarcadorMotorista(localMotorista!!, motorista!!.name)

        val localDestino = LatLng(
            destino!!.latitude!!.toDouble(),
            destino!!.longitude!!.toDouble()
        )
        adicionaMarcadorDestino(localDestino, "Destino")

        centralizarDoisMarcadores(marcadorMotorista, marcadorDestino)
    }

    private fun requisicaoFinalizada() {
        linearLayoutDestino.visibility = View.GONE
        buttonChamarUber.isEnabled = false

        val localDestino = LatLng(
            destino!!.latitude!!.toDouble(),
            destino!!.longitude!!.toDouble()
        )
        adicionaMarcadorDestino(localDestino, "Destino")
        centralizarMarcador(localDestino)

        val localAtual = localMotoristaAtual
        distancia = if (localAtual != null) {
            println("LOCAL PASSAGEIRO$localPassageiro$localAtual")
            Local.calcularDistancia(localPassageiro!!, localAtual).toDouble()
        } else {
            Local.calcularDistancia(localPassageiro!!, localDestino).toDouble()
        }

        val valor = distancia * 8
        val resultado = DecimalFormat("0.00").format(valor)

        buttonChamarUber.text = "Corrida finalizada - R$ $resultado"

        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val view2 = LayoutInflater.from(this)
            .inflate(R.layout.layout_successok_dialog, findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccessOk))
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccessOk).text = resources.getString(R.string.success_title_enc_viagem)
        view2.findViewById<TextView>(R.id.textMessageSuccessOk).text = "Sua viagem ficou: R$ $resultado"
        view2.findViewById<Button>(R.id.buttonSuccessOk).text = "Encerrar Viagem"
        view2.findViewById<ImageView>(R.id.imageIconSuccessOk).setImageResource(R.drawable.logo)

        try {
            val alertDialog = builder.create()

            view2.findViewById<View>(R.id.buttonSuccessOk).setOnClickListener {
                requisicao!!.status = Requisicao.STATUS_ENCERRADA
                requisicao!!.atualizarStatus()
                localMotoristaAtual = null
                alertDialog.dismiss()

                finish()
            }

            alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
            alertDialog.show()
        } catch (e: Exception) {
            println("ERRO" + e.message)
        }
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

    private fun adicionaMarcadorMotorista(localizacao: LatLng, titulo: String?) {
        marcadorMotorista?.remove()

        marcadorMotorista = mMap.addMarker(
            MarkerOptions()
                .position(localizacao)
                .title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.van3))
        )
    }

    private fun adicionaMarcadorDestino(localizacao: LatLng, titulo: String) {
        marcadorPassageiro?.remove()
        marcadorDestino?.remove()

        marcadorDestino = mMap.addMarker(
            MarkerOptions()
                .position(localizacao)
                .title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.chegada))
        )
    }

    private fun centralizarMarcador(local: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 18f))
    }

    private fun centralizarDoisMarcadores(marcador1: Marker?, marcador2: Marker?) {
        val bounds = LatLngBounds.Builder()
            .include(marcador1!!.position)
            .include(marcador2!!.position)
            .build()

        val largura = resources.displayMetrics.widthPixels
        val altura = resources.displayMetrics.heightPixels
        val espacoInterno = (largura * 0.30).toInt()

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, largura, altura, espacoInterno))
    }

    fun chamarUber(view: View) {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(
                this,
                "A localização do seu celular está desligada ou o sinal está muito fraco.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!isOnline(this)) {
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
            return
        }

        if (cancelarUber) {
            if (tipoRequisicao == "aguardando") {
                requisicao!!.status = Requisicao.STATUS_CANCELADA
                requisicao!!.atualizarStatus()
                requisicao!!.excluirHistorico()
            }
        } else {
            getCompleteAddressString(_latitude, _longitude)

            val enderecoDestino = editDestino.text.toString()

            if (enderecoDestino != "") {
                val dataFormatada = SimpleDateFormat("dd/MM/yyyy - HH:mm").format(Date())

                val addressDestino = recuperarEndereco(enderecoDestino)
                if (addressDestino != null) {
                    val destino = Destino()
                    destino.data = dataFormatada
                    destino.cidade = addressDestino.subAdminArea ?: "N/A"
                    destino.cep = addressDestino.postalCode ?: "N/A"
                    destino.bairro = addressDestino.subLocality ?: "N/A"
                    destino.rua = addressDestino.thoroughfare ?: "N/A"
                    destino.numero = addressDestino.featureName ?: "N/A"
                    destino.latitude = addressDestino.latitude.toString()
                    destino.longitude = addressDestino.longitude.toString()

                    val mensagem = StringBuilder()
                    mensagem.append("Cidade: " + (destino.cidade ?: "N/A"))
                    mensagem.append("\nRua: " + (destino.rua ?: "N/A"))
                    mensagem.append("\nBairro: " + (destino.bairro ?: "N/A"))

                    val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    val view2 = LayoutInflater.from(this)
                        .inflate(R.layout.layout_success_dialog, findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccessOk))
                    builder.setView(view2)
                    view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.success_title_confirme_viagem)
                    view2.findViewById<TextView>(R.id.textMessageSuccess).text = mensagem
                    view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
                    view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
                    view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

                    val alertDialog = builder.create()

                    view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
                        alertDialog.dismiss()
                        salvarRequisicao(destino)

                        Requisicao().salvarHistorico(destino)
                    }

                    view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
                        alertDialog.dismiss()
                    }

                    alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
                    alertDialog.show()
                }
            } else {
                Toast.makeText(this, "Informe o endereço de destino!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double) {
        try {
            val addresses = Geocoder(this, Locale.getDefault()).getFromLocation(LATITUDE, LONGITUDE, 1)

            if (!addresses.isNullOrEmpty()) {
                endereco = addresses[0].thoroughfare
                cidade = addresses[0].subAdminArea
                bairro = addresses[0].subLocality
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun salvarRequisicao(destino: Destino) {
        val requisicao = Requisicao()
        requisicao.destino = destino

        val usuarioPassageiro = UsuarioFirebase.getDadosUsuarioLogado()
        usuarioPassageiro.latitude = localPassageiro!!.latitude.toString()
        usuarioPassageiro.longitude = localPassageiro!!.longitude.toString()
        usuarioPassageiro.enderecoAtualUsuario = endereco
        usuarioPassageiro.cidadeAtualUsuario = cidade
        usuarioPassageiro.bairroAtualUsuario = bairro

        requisicao.passageiro = usuarioPassageiro
        requisicao.status = Requisicao.STATUS_AGUARDANDO
        requisicao.salvar()

        linearLayoutDestino.visibility = View.GONE
        buttonChamarUber.text = "Cancelar Mioper"
    }

    private fun recuperarEndereco(endereco: String): Address? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val listaEnderecos = geocoder.getFromLocationName(endereco, 1)
            if (!listaEnderecos.isNullOrEmpty()) {
                return listaEnderecos[0]
            } else {
                Toast.makeText(this, "Ops! Endereço não encontrado.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun recuperarLocalizacaoUsuario() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                _latitude = latitude
                _longitude = longitude

                localPassageiro = LatLng(latitude, longitude)

                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude)

                alteraInterfaceStatusRequisicao(statusRequisicao)

                val status = statusRequisicao
                if (!status.isNullOrEmpty()) {
                    if (status == Requisicao.STATUS_VIAGEM || status == Requisicao.STATUS_FINALIZADA) {
                        locationManager.removeUpdates(locationListener)
                    } else if (ActivityCompat.checkSelfPermission(
                            this@Passageiro,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            10000,
                            10f,
                            locationListener
                        )
                    }
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
        editDestino = findViewById(R.id.editDestino)
        linearLayoutDestino = findViewById(R.id.linearLayoutDestino)
        buttonChamarUber = findViewById(R.id.buttonChamarUber)

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    companion object {
        var endereco: String? = null
        var cidade: String? = null
        var bairro: String? = null

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }
}
