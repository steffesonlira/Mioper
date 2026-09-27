package com.cursoandroid.mioper.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.cursoandroid.mioper.R
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase
import com.cursoandroid.mioper.firebase.UsuarioFirebase
import com.cursoandroid.mioper.model.Mask
import com.cursoandroid.mioper.model.UserProfile
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MeusDados : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var celularUsuario: EditText
    var senhaUsuario: String? = null
    lateinit var emailUsuario: EditText
    lateinit var enderecoUsuario: EditText
    lateinit var dataNascimentoUsuario: EditText
    lateinit var cpfUsuario: EditText
    lateinit var generoUsuario: Switch
    var tipoUsuario: String? = null
    var idUsuario: String? = null
    lateinit var txtMasculino: TextView
    lateinit var txtFeminino: TextView
    var email: String? = null
    var nome: String? = null
    private lateinit var autenticacao: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_meus_dados)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        nomeUsuario = findViewById(R.id.txtNome)
        celularUsuario = findViewById(R.id.txtCelular)
        txtFeminino = findViewById(R.id.txtFeminino)
        txtMasculino = findViewById(R.id.txtMasculino)
        emailUsuario = findViewById(R.id.txtEmail)
        enderecoUsuario = findViewById(R.id.txtEndereco)
        dataNascimentoUsuario = findViewById(R.id.txtDataNascimento)
        cpfUsuario = findViewById(R.id.txtCpf)
        generoUsuario = findViewById(R.id.switchGenero)

        generoUsuario.setOnCheckedChangeListener { _, _ ->
            if (generoUsuario.isChecked) {
                txtMasculino.setTextColor(Color.parseColor("#009688"))
                txtFeminino.setTextColor(Color.parseColor("#FFFFFFFF"))
            } else {
                txtFeminino.setTextColor(Color.parseColor("#009688"))
                txtMasculino.setTextColor(Color.parseColor("#FFFFFFFF"))
            }
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Meus Dados"

        val dados = intent.extras!!
        nome = dados.getString("name")
        val celular = dados.getString("mobile")
        email = dados.getString("email")
        val endereco = dados.getString("adress")
        senhaUsuario = dados.getString("senha")
        val nascimento = dados.getString("nascimento")
        val cpf = dados.getString("cpf")
        tipoUsuario = dados.getString("tipouser")

        nomeUsuario.setText(nome)
        celularUsuario.setText(celular)
        emailUsuario.setText(email)
        enderecoUsuario.setText(endereco)
        dataNascimentoUsuario.setText(nascimento)
        cpfUsuario.setText(cpf)

        celularUsuario.addTextChangedListener(Mask.mask(celularUsuario, Mask.FORMAT_FONE))
        cpfUsuario.addTextChangedListener(Mask.mask(cpfUsuario, Mask.FORMAT_CPF))
        dataNascimentoUsuario.addTextChangedListener(Mask.mask(dataNascimentoUsuario, Mask.FORMAT_DATE))
    }

    fun salvarAlteracoes(view: View) {
        if (!isOnline(this)) {
            Toast.makeText(
                this,
                "Alterações não foram salvas. Verifique sua conexão com a internet. ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val textoNome = nomeUsuario.text.toString()
        val textoEmail = emailUsuario.text.toString()
        val textoEndereco = enderecoUsuario.text.toString()
        val textoCelular = celularUsuario.text.toString()
        val textoDataNascimento = dataNascimentoUsuario.text.toString()
        val textoCpf = cpfUsuario.text.toString()

        if (textoNome.isEmpty()) {
            Toast.makeText(this@MeusDados, "Preencha o Nome!", Toast.LENGTH_SHORT).show()
            return
        }
        if (textoEndereco.isEmpty()) {
            Toast.makeText(this@MeusDados, "Preencha o Endereço!", Toast.LENGTH_SHORT).show()
            return
        }
        if (textoEmail.isEmpty()) {
            Toast.makeText(this@MeusDados, "Preencha o E-mail!", Toast.LENGTH_SHORT).show()
            return
        }
        if (textoCelular.isEmpty()) {
            Toast.makeText(this@MeusDados, "Preencha o Celular!", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this@MeusDados, R.style.AlertDialogTheme)
        val view2 = layoutInflater.inflate(
            R.layout.layout_success_dialog,
            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccess)
        )
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.warning_title)
        view2.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc)
        view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
        view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
        view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

        val alertDialog = builder.create()

        view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
            alertDialog.dismiss()
            val usuario = UserProfile()
            usuario.name = textoNome
            usuario.email = textoEmail
            usuario.adress = textoEndereco
            usuario.mobile = textoCelular
            usuario.nascimento = textoDataNascimento
            usuario.cpf = textoCpf
            usuario.senha = senhaUsuario
            usuario.genero = verificaGeneroUsuario()
            usuario.tipouser = tipoUsuario

            cadastrarUsuario(usuario)
            Toast.makeText(this@MeusDados, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show()
        }

        view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()
    }

    fun cadastrarUsuario(usuario: UserProfile) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        val currentUser = autenticacao.currentUser ?: return

        try {
            idUsuario = currentUser.uid
            usuario.id = idUsuario
            usuario.salvar()

            UsuarioFirebase.atualizarNomeUsuario(usuario.name!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun alterarSenhaUsuario(view: View) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        idUsuario = autenticacao.currentUser!!.uid

        val i = Intent(this, AlterarSenha::class.java)
        i.putExtra("idUsuario", idUsuario)
        i.putExtra("senha", senhaUsuario)
        i.putExtra("email", email)

        startActivity(i)
    }

    fun excluirUsuario(view: View) {
        if (!isOnline(this)) {
            Toast.makeText(
                this,
                "Erro ao excluir sua conta. Verifique sua conexão com a internet. ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val builder = AlertDialog.Builder(this@MeusDados, R.style.AlertDialogTheme)
        val view2 = layoutInflater.inflate(
            R.layout.layout_success_dialog,
            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccess)
        )
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccess).text = resources.getString(R.string.warning_title_excluir_conta)
        view2.findViewById<TextView>(R.id.textMessageSuccess).text = resources.getString(R.string.text_desc_excluir_conta)
        view2.findViewById<Button>(R.id.buttonConfirmaSuccess).text = resources.getString(R.string.confirmar)
        view2.findViewById<Button>(R.id.buttonCancelSuccess).text = resources.getString(R.string.cancelar)
        view2.findViewById<ImageView>(R.id.imageIconSuccess).setImageResource(R.drawable.logo)

        val alertDialog = builder.create()

        view2.findViewById<View>(R.id.buttonConfirmaSuccess).setOnClickListener {
            alertDialog.dismiss()
            excluirConta()
        }

        view2.findViewById<View>(R.id.buttonCancelSuccess).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()
    }

    fun excluirConta() {
        // O id precisa ser lido antes do delete(), que desloga o usuário
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        idUsuario = autenticacao.currentUser!!.uid

        val user = FirebaseAuth.getInstance().currentUser!!
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                excluirInformacoesEsuario()
                exibirDialogoConclusao(
                    R.string.warning_title_excluir_ok_conta,
                    R.string.text_desc_excluir_ok_conta
                )
            } else {
                exibirDialogoConclusao(
                    R.string.warning_title_tempo_excluir_ok_conta,
                    R.string.text_desc_tempo_excluir_ok_conta
                )
            }
        }
    }

    private fun exibirDialogoConclusao(titulo: Int, mensagem: Int) {
        val builder = AlertDialog.Builder(this@MeusDados, R.style.AlertDialogTheme)
        val view2 = layoutInflater.inflate(
            R.layout.layout_successok_dialog,
            findViewById<ConstraintLayout>(R.id.layoutDialogContainerSuccessOk)
        )
        builder.setView(view2)
        view2.findViewById<TextView>(R.id.textTitleSuccessOk).text = resources.getString(titulo)
        view2.findViewById<TextView>(R.id.textMessageSuccessOk).text = resources.getString(mensagem)
        view2.findViewById<Button>(R.id.buttonSuccessOk).text = resources.getString(R.string.confirmar)
        view2.findViewById<ImageView>(R.id.imageIconSuccessOk).setImageResource(R.drawable.logo)

        val alertDialog = builder.create()

        view2.findViewById<View>(R.id.buttonSuccessOk).setOnClickListener {
            alertDialog.dismiss()
            fecharTela()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.show()
    }

    fun excluirInformacoesEsuario() {
        try {
            val usuario = UserProfile()
            usuario.id = idUsuario
            usuario.remover()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fecharTela() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, Login::class.java))
    }

    fun verificaGeneroUsuario(): String {
        return if (generoUsuario.isChecked) "Feminino" else "Masculino"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (tipoUsuario == "M") {
                    startActivity(Intent(this, Requisicoes::class.java))
                } else {
                    Principal.verificaRetorno = true
                    finish()
                }
            }
        }
        return true
    }

    override fun onBackPressed() {
        Principal.verificaRetorno = true
        finish()
    }

    companion object {
        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }
}
