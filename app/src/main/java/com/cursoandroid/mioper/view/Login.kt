package com.cursoandroid.mioper.view

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cursoandroid.mioper.Cadastro
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase
import com.cursoandroid.mioper.model.Permissoes
import com.cursoandroid.mioper.model.UserProfile
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference

class Login : AppCompatActivity() {

    var Email_User: String? = null
    var Tipo_User: String? = null
    lateinit var email: EditText
    lateinit var senha: EditText
    private lateinit var logar: Button
    private var logarFace: LoginButton? = null
    private lateinit var criarConta: TextView
    private lateinit var esqueceuSenha: TextView
    lateinit var viewLayout: View
    private var txtName: TextView? = null
    private var txtEmail: TextView? = null
    var databaseReference: DatabaseReference? = null
    lateinit var mAuth: FirebaseAuth
    var progressBar: ProgressBar? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var callbackManager: CallbackManager
    private var backPressedTime: Long = 0
    private val permissoes = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseAuth.getInstance().signOut()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        viewLayout = layoutInflater.inflate(R.layout.customtoast, findViewById<ViewGroup>(R.id.custom_layout))
        email = findViewById(R.id.edtUsuario)
        senha = findViewById(R.id.edtSenha)
        logar = findViewById(R.id.btnLogin)
        criarConta = findViewById(R.id.link_signup)
        esqueceuSenha = findViewById(R.id.txtEsqueciSenhaId)
        mAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        Permissoes.validarPermissoes(permissoes, this, 1)

        criarConta.setOnClickListener {
            startActivity(Intent(this@Login, Cadastro::class.java))
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }

        esqueceuSenha.setOnClickListener {
            startActivity(Intent(this@Login, EsqueciSenha::class.java))
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    fun SignInGoogle() {
        val signIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signIntent, GOOGLE_SIGN)
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().signOut()
        UsuarioFirebase.redirecionaUsuarioLogado(this@Login)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (permissaoResultado in grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao()
            }
        }
    }

    private fun alertaValidacaoPermissao() {
        AlertDialog.Builder(this)
            .setTitle("Permissões Negadas")
            .setMessage("Para utilizar o app é necessário aceitar as permissões")
            .setCancelable(false)
            .setPositiveButton("Confirmar") { _, _ -> finish() }
            .create()
            .show()
    }

    fun validarLoginUsuario(view: View) {
        val textoEmail = email.text.toString()
        val textoSenha = senha.text.toString()

        if (textoEmail.isNotEmpty()) {
            if (textoSenha.isNotEmpty()) {
                val usuario = UserProfile()
                usuario.email = textoEmail
                usuario.senha = textoSenha

                logarUsuario(usuario)
            } else {
                Toast.makeText(this@Login, "Preencha a senha!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@Login, "Preencha o email!", Toast.LENGTH_SHORT).show()
        }
    }

    fun logarUsuario(usuario: UserProfile) {
        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao()
        mAuth.signInWithEmailAndPassword(usuario.email!!, usuario.senha!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val progressDialog = ProgressDialog(this@Login, R.style.AppCompatAlertDialogStyle)
                    progressDialog.isIndeterminate = true
                    progressDialog.setIcon(R.drawable.logo)
                    progressDialog.setTitle("Mioper")
                    progressDialog.setMessage("Realizando o Login...")
                    progressDialog.show()

                    Handler().postDelayed({
                        // A activity pode ter sido destruída antes dos 10s, e o dismiss lançaria exceção
                        try {
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()
                            }
                        } catch (e: Exception) {
                        }
                    }, 10000)

                    UsuarioFirebase.redirecionaUsuarioLogado(this@Login)
                } else {
                    val excecao = when (val e = task.exception) {
                        is FirebaseAuthInvalidUserException -> "Usuário não está cadastrado."
                        is FirebaseAuthInvalidCredentialsException -> "E-mail e senha não correspondem a um usuário cadastrado"
                        else -> {
                            e?.printStackTrace()
                            "Verifique sua conexão com a internet"
                        }
                    }
                    Toast.makeText(this@Login, excecao, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        const val GOOGLE_SIGN = 101
        private const val TAG = "GoogleActivity"
    }
}
