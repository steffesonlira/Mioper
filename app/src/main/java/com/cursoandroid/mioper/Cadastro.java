package com.cursoandroid.mioper;

//region IMPORT
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import butterknife.BindView;
import butterknife.ButterKnife;
//endregion

public class Cadastro extends AppCompatActivity {

    //region Variáveis cadastro do usuário - Inicializando com BindView
    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    private FirebaseAuth mAuth;
    private Switch switchTipoUsuario;
    //endregion

    //region onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        ButterKnife.bind(this);

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Retornar ao Login");
        //endregion

        //region atribuindo às variáveis, características virtuais da activity
        switchTipoUsuario = findViewById(R.id.switchTipoUsuario);
        //Inserindo máscara ao campo de digitação Celular
        _mobileText.addTextChangedListener(Mask.mask(_mobileText, Mask.FORMAT_FONE));
        _signupButton = findViewById(R.id.btn_signup);




        //region Click botão cadastrar
            _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                validate();//método para realizar a validação dos campos
                signup(); //Realizar o cadastro
            }
        });
        //endregion


        //region Click textView para ir ate a tela de login
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    //endregion

    //region Método CreateUserAccount()
    private void CreateUserAccount(final UserProfile usuario) {
        String nome = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String email = _emailText.getText().toString();
        String senha = _passwordText.getText().toString();
        String repitasenha = _reEnterPasswordText.getText().toString();

        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setName(nome);
        usuario.setAdress(address);
        usuario.setMobile(mobile);
        usuario.setRepitasenha(repitasenha);
        usuario.setTipouser(verificaTipoUsuario() );

        mAuth =  ConfiguracaoFirebase.getFirebaseAutenticacao();
        mAuth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {

                try {

                    String idUsuario = task.getResult().getUser().getUid();
                    usuario.setId(idUsuario);
                    usuario.salvar();
                    //Atualizar nome no UserProfile
                    UsuarioFirebase.atualizarNomeUsuario(usuario.getName());
                    // Redireciona o usuário com base no seu tipo
                    // Se o usuário for passageiro chama a activity maps
                    // senão chama a activity requisicoes
                    if (verificaTipoUsuario() == "P") {
                        startActivity(new Intent(Cadastro.this, Principal.class));
                        finish();
                        Toast.makeText(Cadastro.this,
                                "Sucesso ao cadastrar Passageiro!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(Cadastro.this, Requisicoes.class));
                        finish();
                        Toast.makeText(Cadastro.this,
                                "Sucesso ao cadastrar Motorista!",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                String excecao = "";
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    excecao = "Digite uma senha mais forte!";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    excecao = "Por favor, digite um e-mail válido";
                } catch (FirebaseAuthUserCollisionException e) {
                    excecao = "Este conta já foi cadastrada";
                } catch (Exception e) {
                    excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(Cadastro.this,
                        excecao,
                        Toast.LENGTH_SHORT).show();
            }

        });

    }
    //endregion

    //region validação de campos Criação de ProgressDialog para redirecionar após realização de cadastro signup()
    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(Cadastro.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Criando sua conta...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
//endregion

    //region Metodo onSignupSuccess()
    public void onSignupSuccess() {
        //registrar dados inseridos
        UserProfile usuario = new UserProfile();
        CreateUserAccount(usuario);
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

    }
//endregion

    //region Metodo onSignupFailed()
    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Falha na criação do cadastro", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        return;
    }
    //endregion

    //region Validação de dados validate()
    public boolean validate() {
        boolean valid = true;

        String textoNome = _nameText.getText().toString();
        String textoEmail = _emailText.getText().toString();
        String textoEndereco = _addressText.getText().toString();
        String textoCelular = _mobileText.getText().toString();
        String textoSenha = _passwordText.getText().toString();
        String textoRepitaSenha = _reEnterPasswordText.getText().toString();

        UserProfile usuarios = new UserProfile();
        usuarios.setName(textoNome);
        usuarios.setEmail(textoEmail);
        usuarios.setAdress(textoEndereco);
        usuarios.setMobile(textoCelular);
        usuarios.setSenha(textoSenha);
        usuarios.setRepitasenha(textoRepitaSenha);
        usuarios.setTipouser( verificaTipoUsuario() );
        usuarios.setNascimento("");
        usuarios.setCpf("");
        usuarios.setGenero("");

        if (usuarios.getName().isEmpty() || usuarios.getName().length() < 3) {
            _nameText.setError("Entre com pelo menos 3 caracteres");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (usuarios.getAdress().isEmpty()) {
            _addressText.setError("Entre com um endereço válido");
            valid = false;
        } else {
            _addressText.setError(null);
        }

        if (usuarios.getEmail().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(usuarios.getEmail()).matches()) {
            _emailText.setError("Entre com um e-mail válido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (usuarios.getMobile().isEmpty() || usuarios.getMobile().length()<11) {
            _mobileText.setError("Entre com um número de telefone corretamente");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (usuarios.getSenha().isEmpty() || usuarios.getSenha().length() < 4 || usuarios.getSenha().length() > 10) {
            _passwordText.setError("A senha deve ser entre 4 e 10 caracteres");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (usuarios.repitasenha.isEmpty() || usuarios.repitasenha.length() < 4 || usuarios.repitasenha.length() > 10 || !(usuarios.repitasenha.equals(usuarios.getSenha()))) {
            _reEnterPasswordText.setError("Senha não confere com a digitada acima");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }
        return valid;

    }
    //endregion

    //region Voltar botão Android
    @Override
    public void onBackPressed() {
        Intent h= new Intent(Cadastro.this,Login.class);
        startActivity(h);
    }
    //endregion

    //region Criação do Menu Toolbar XML
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal
                , menu);
        return true;
    }
    //endregion

    //region verificaTipoUser()
    public String verificaTipoUsuario() {
        return switchTipoUsuario.isChecked() ? "M" : "P";
    }
    //endregion

    //region onOptionsItemSelected() Ao clicar na seta voltar do toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, Login.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }
    //endregion
}
