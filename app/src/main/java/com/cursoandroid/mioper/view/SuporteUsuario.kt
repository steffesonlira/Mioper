package com.cursoandroid.mioper.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cursoandroid.mioper.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class SuporteUsuario : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var txtPara: EditText
    private lateinit var txtMensagem: EditText
    private lateinit var assunto: Spinner
    private lateinit var btnMensagem: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suporteusuario)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        mAuth = FirebaseAuth.getInstance()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        txtPara = findViewById(R.id.idPara)
        txtMensagem = findViewById(R.id.idMensagem)
        btnMensagem = findViewById(R.id.btnEnviarMensagem)
        assunto = findViewById(R.id.idAssunto)
        val adapter = ArrayAdapter.createFromResource(this, R.array.assunto, R.layout.simple_custom_spinner2)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assunto.adapter = adapter
        assunto.onItemSelectedListener = this

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Suporte ao Usuário"

        btnMensagem.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("mailto:")

            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(txtPara.text.toString()))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, assunto.selectedItem.toString())
            emailIntent.putExtra(Intent.EXTRA_TEXT, txtMensagem.text.toString())
            emailIntent.selector = intent
            startActivity(Intent.createChooser(emailIntent, "Enviar E-mail via..."))
            txtMensagem.setText("")
        }
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}
