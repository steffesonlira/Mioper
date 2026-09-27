package com.cursoandroid.mioper.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase

class CadastrarCartaoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var numCartao: EditText
    private lateinit var dataVencimentoCartao: EditText
    private lateinit var codNumCartao: EditText
    private lateinit var confirmar: Button
    private lateinit var paisesCadastrados: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_cartao)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Cadastro de Cartões"

        paisesCadastrados = findViewById(R.id.paisesCadastradosID)
        val adapter = ArrayAdapter.createFromResource(this, R.array.paises, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paisesCadastrados.adapter = adapter
        paisesCadastrados.onItemSelectedListener = this

        confirmar = findViewById(R.id.botaoConfirmarCadastroCartaoID)
        confirmar.setOnClickListener {
            numCartao = findViewById(R.id.numeroCartaoID)
            dataVencimentoCartao = findViewById(R.id.dataVencimentoCartaoID)
            codNumCartao = findViewById(R.id.codigoSegurancaCartaoID)

            salvarCartaoCadastrado(numCartao.text.toString())
        }
    }

    fun salvarCartaoCadastrado(numeroDoCartao: String) {
        if (!isOnline(this)) {
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this@CadastrarCartaoActivity, R.style.AlertDialogTheme)
        val view2 = LayoutInflater.from(this@CadastrarCartaoActivity)
            .inflate(R.layout.layout_success_dialog, findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccess))
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.success_title_cadastro_cartao)
        view2.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc_cadastro_cartao)
        view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
        view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
        view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

        val alertDialog = builder.create()

        view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
            alertDialog.dismiss()
            val firebaseUser = UsuarioFirebase.getUsuarioAtual()!!
            val firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()

            val userMailReplaced = firebaseUser.email!!.replace('.', '-')

            val metodosPagamentos = firebaseRef.child("CartoesPagamentos").child(userMailReplaced)

            metodosPagamentos.child(numeroDoCartao).setValue(numeroDoCartao)

            Toast.makeText(this@CadastrarCartaoActivity, "Cartão cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
            MudarTelaAoSalvar()
        }

        view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()
    }

    fun ValidaCartao(numeroCartao: String?) {
    }

    fun MudarTelaAoSalvar() {
        startActivity(Intent(this, MetodosDePagamentoActivity::class.java))
        finish()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val paisSelecionado = parent!!.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, MetodosDePagamentoActivity::class.java))
                finish()
            }
        }
        return true
    }

    companion object {
        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }
}
