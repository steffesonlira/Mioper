package com.cursoandroid.mioper

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase
import com.cursoandroid.mioper.model.Mask
import com.cursoandroid.mioper.model.UserProfile
import com.cursoandroid.mioper.view.Login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class Cadastro : AppCompatActivity() {

    lateinit var _nameText: EditText
    lateinit var _addressText: EditText
    lateinit var _emailText: EditText
    lateinit var _mobileText: EditText
    lateinit var _passwordText: EditText
    lateinit var _reEnterPasswordText: EditText
    lateinit var _signupButton: Button
    lateinit var _loginLink: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var switchTipoUsuario: Switch
    lateinit var passageiro: TextView
    lateinit var motorista: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        _nameText = findViewById(R.id.input_name)
        _addressText = findViewById(R.id.input_address)
        _emailText = findViewById(R.id.input_email)
        _mobileText = findViewById(R.id.input_mobile)
        _passwordText = findViewById(R.id.input_password)
        _reEnterPasswordText = findViewById(R.id.input_reEnterPassword)
        _signupButton = findViewById(R.id.btn_signup)
        _loginLink = findViewById(R.id.link_login)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Cadastro do Novo Mioper"

        switchTipoUsuario = findViewById(R.id.switchTipoUsuario)
        passageiro = findViewById(R.id.nomeUsuário)
        motorista = findViewById(R.id.txtCadastrar)

        switchTipoUsuario.setOnCheckedChangeListener { _, _ ->
            if (switchTipoUsuario.isChecked) {
                passageiro.setTextColor(Color.parseColor("#009688"))
                motorista.setTextColor(Color.parseColor("#FFFFFFFF"))
            } else {
                motorista.setTextColor(Color.parseColor("#009688"))
                passageiro.setTextColor(Color.parseColor("#FFFFFFFF"))
            }
        }

        _mobileText.addTextChangedListener(Mask.mask(_mobileText, Mask.FORMAT_FONE))

        _signupButton.setOnClickListener {
            validate()
            signup()
        }

        _loginLink.setOnClickListener {
            startActivity(Intent(applicationContext, Login::class.java))
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    private fun CreateUserAccount(usuario: UserProfile) {
        usuario.email = _emailText.text.toString()
        usuario.senha = _passwordText.text.toString()
        usuario.name = _nameText.text.toString()
        usuario.adress = _addressText.text.toString()
        usuario.mobile = _mobileText.text.toString()
        usuario.tipouser = verificaTipoUsuario()

        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao()
        mAuth.createUserWithEmailAndPassword(usuario.email!!, usuario.senha!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    try {
                        usuario.id = task.result!!.user!!.uid
                        usuario.salvar()

                        UsuarioFirebase.atualizarNomeUsuario(usuario.name!!)

                        if (verificaTipoUsuario() == "P") {
                            FirebaseAuth.getInstance().signOut()
                            startActivity(Intent(this@Cadastro, Login::class.java))
                            finish()

                            // Passageiro novo já nasce com "Dinheiro" como método de pagamento.
                            // Após o signOut acima getUsuarioAtual() é nulo, então isso cai no catch (comportamento original)
                            val firebaseUser = UsuarioFirebase.getUsuarioAtual()!!
                            val userMailReplaced = firebaseUser.email!!.replace('.', '-')

                            val metodosPagamentos = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("CartoesPagamentos")
                                .child(userMailReplaced)

                            val idUsuarioDoCartao = metodosPagamentos.push().key!!
                            metodosPagamentos.child(idUsuarioDoCartao).setValue("Dinheiro")

                            Toast.makeText(this@Cadastro, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            startActivity(Intent(this@Cadastro, Login::class.java))
                            FirebaseAuth.getInstance().signOut()
                            finish()
                            Toast.makeText(this@Cadastro, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val excecao = when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> "Digite uma senha mais forte!"
                        is FirebaseAuthInvalidCredentialsException -> "Por favor, digite um e-mail válido"
                        is FirebaseAuthUserCollisionException -> "Este conta já foi cadastrada"
                        else -> "Verifique sua conexão com a internet"
                    }
                    Toast.makeText(this@Cadastro, excecao, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signup() {
        if (!validate()) {
            onSignupFailed()
            return
        }

        val progressDialog = ProgressDialog(this@Cadastro, R.style.AppCompatAlertDialogStyle)
        progressDialog.isIndeterminate = true
        progressDialog.setIcon(R.drawable.logo)
        progressDialog.setTitle("Mioper")
        progressDialog.setMessage("Criando sua conta...")
        progressDialog.show()

        Handler().postDelayed({
            onSignupSuccess()
            progressDialog.dismiss()
        }, 3000)
    }

    fun onSignupSuccess() {
        CreateUserAccount(UserProfile())
        _signupButton.isEnabled = true
        setResult(RESULT_OK, null)
    }

    fun onSignupFailed() {
        Toast.makeText(baseContext, "Falha na criação do cadastro", Toast.LENGTH_LONG).show()
        _signupButton.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val textoNome = _nameText.text.toString()
        val textoEmail = _emailText.text.toString()
        val textoEndereco = _addressText.text.toString()
        val textoCelular = _mobileText.text.toString()
        val textoSenha = _passwordText.text.toString()
        val textoRepitaSenha = _reEnterPasswordText.text.toString()

        if (textoNome.isEmpty() || textoNome.length < 3) {
            _nameText.error = "Entre com pelo menos 3 caracteres"
            valid = false
        } else {
            _nameText.error = null
        }

        if (textoEndereco.isEmpty()) {
            _addressText.error = "Entre com um endereço válido"
            valid = false
        } else {
            _addressText.error = null
        }

        if (textoEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(textoEmail).matches()) {
            _emailText.error = "Entre com um e-mail válido"
            valid = false
        } else {
            _emailText.error = null
        }

        if (textoCelular.isEmpty() || textoCelular.length < 11) {
            _mobileText.error = "Entre com um número de telefone válido"
            valid = false
        } else {
            _mobileText.error = null
        }

        if (textoSenha.isEmpty() || textoSenha.length < 4 || textoSenha.length > 10) {
            _passwordText.error = "A senha deve possuir entre 4 e 10 caracteres"
            valid = false
        } else {
            _passwordText.error = null
        }

        if (textoRepitaSenha.isEmpty() || textoRepitaSenha.length < 4 || textoRepitaSenha.length > 10 || textoRepitaSenha != textoSenha) {
            _reEnterPasswordText.error = "A senha não confere com a digitada acima"
            valid = false
        } else {
            _reEnterPasswordText.error = null
        }
        return valid
    }

    override fun onBackPressed() {
        startActivity(Intent(this@Cadastro, Login::class.java))
        finish()
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
    }

    fun verificaTipoUsuario(): String {
        return if (switchTipoUsuario.isChecked) "M" else "P"
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
}
