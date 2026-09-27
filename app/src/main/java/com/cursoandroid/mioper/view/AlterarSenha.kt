package com.cursoandroid.mioper.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.google.firebase.auth.FirebaseAuth

class AlterarSenha : AppCompatActivity() {

    lateinit var senhaAntiga: EditText
    lateinit var senhaNova: EditText
    lateinit var confirmarSenha: EditText
    var userID: String? = null
    var senha: String? = null
    var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterar_senha)

        senhaAntiga = findViewById(R.id.txtSenhaAntiga)
        senhaNova = findViewById(R.id.txtSenhaNova)
        confirmarSenha = findViewById(R.id.txtConfirmarSenha)

        senhaAntiga.requestFocus()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Alteração de Senha"

        val dados = intent.extras!!
        userID = dados.getString("idUsuario")
        senha = dados.getString("senha")
        email = dados.getString("email")
    }

    fun AlterarSenha(view: View) {
        if (!isOnline(this)) {
            Toast.makeText(
                this,
                "Erro ao alterar sua senha. Verifique sua conexão com a internet.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

        val _SenhaAntiga = senhaAntiga.text.toString()
        val _SenhaNova = senhaNova.text.toString()
        val _ConfirmarSenha = confirmarSenha.text.toString()

        if (_SenhaAntiga.isEmpty()) {
            Toast.makeText(this@AlterarSenha, "Digite a senha antiga!", Toast.LENGTH_SHORT).show()
            return
        }

        if (_SenhaNova.isEmpty()) {
            Toast.makeText(this@AlterarSenha, "Digite a senha nova!", Toast.LENGTH_SHORT).show()
            return
        }

        if (_ConfirmarSenha.isEmpty()) {
            Toast.makeText(this@AlterarSenha, "Confirme a senha!", Toast.LENGTH_SHORT).show()
            return
        }

        if (_SenhaNova != _ConfirmarSenha) {
            Toast.makeText(this@AlterarSenha, "Sua nova senha não coincide!", Toast.LENGTH_SHORT).show()
            return
        }

        if (_SenhaAntiga != senha) {
            Toast.makeText(this@AlterarSenha, "Senha antiga invalida!", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this@AlterarSenha, R.style.AlertDialogTheme)
        val view2 = layoutInflater.inflate(
            R.layout.layout_success_dialog,
            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccessOk)
        )
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.warning_title_redefinir_ok_conta)
        view2.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc_redefinir_ok_conta)
        view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
        view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
        view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

        val alertDialog = builder.create()

        view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
            ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Users")
                .child(userID!!)
                .child("senha")
                .setValue(senhaNova.text.toString())

            // A senha do Auth só pode ser trocada por e-mail de redefinição sem reautenticar o usuário
            FirebaseAuth.getInstance().sendPasswordResetEmail(email!!)

            alertDialog.dismiss()
            finish()
        }

        view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()
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
        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }
}
