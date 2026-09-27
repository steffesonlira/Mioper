package com.cursoandroid.mioper.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cursoandroid.mioper.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class EsqueciSenha : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var buttonEsqueciSenha: Button
    private var userEmail: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esqueci_senha)
        firebaseAuth = FirebaseAuth.getInstance()
        email = findViewById(R.id.edtEsqueciSenhaId)
        buttonEsqueciSenha = findViewById(R.id.btnRecuperarSenhaId)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Retornar ao Login"

        buttonEsqueciSenha.setOnClickListener {
            if (email.text.toString() == "") {
                Toast.makeText(this@EsqueciSenha, "Favor inserir o e-mail para redefinição de senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            firebaseAuth
                .sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val progressDialog = ProgressDialog(this@EsqueciSenha, R.style.AppCompatAlertDialogStyle)
                        progressDialog.isIndeterminate = true
                        progressDialog.setIcon(R.drawable.logo)
                        progressDialog.setTitle("Mioper")
                        progressDialog.setMessage("Enviando e-mail para recuperação de senha...")
                        progressDialog.show()

                        Handler().postDelayed({
                            progressDialog.dismiss()
                            Toast.makeText(this@EsqueciSenha, "Solicitação enviada com sucesso para o seu e-mail", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@EsqueciSenha, Login::class.java))
                            finish()
                        }, 2500)
                    } else {
                        val excecao = when (task.exception) {
                            is FirebaseAuthInvalidUserException -> "Usuário não está cadastrado."
                            else -> "Verifique sua conexão com a internet"
                        }
                        Toast.makeText(this@EsqueciSenha, excecao, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, Login::class.java))
                finish()
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
            }
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
    }
}
