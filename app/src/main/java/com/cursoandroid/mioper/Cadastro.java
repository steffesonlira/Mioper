package com.cursoandroid.mioper;

//region IMPORTs

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_address)
    EditText _addressText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_mobile)
    EditText _mobileText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    private FirebaseAuth mAuth;
    private Switch switchTipoUsuario;
    TextView passageiro;
    TextView motorista;


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        ButterKnife.bind(this);


        switchTipoUsuario = findViewById(R.id.switchTipoUsuario);
        passageiro = findViewById(R.id.nomeUsuário);
        motorista = findViewById(R.id.txtCadastrar);


        //AO CLICAR, ALTERA A COR DO SWITCH PASSGEIRO/MOTORISTA
        switchTipoUsuario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchTipoUsuario.isChecked()) {
                    passageiro.setTextColor(Color.parseColor("#009688"));
                    motorista.setTextColor(Color.parseColor("#FFFFFFFF"));

                } else {
                    motorista.setTextColor(Color.parseColor("#009688"));
                    passageiro.setTextColor(Color.parseColor("#FFFFFFFF"));

                }
            }
        });

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


    //region Método para criação de usuário e senha no Firebase pelo método de Authentication
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
        usuario.setTipouser(verificaTipoUsuario());


        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
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

    //Chamando função de validação de campos (Criação de ProgressDialog para redirecionar após realização de cadastro
    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(Cadastro.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Criando sua conta...");
        progressDialog.show();


        //endregion

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);

    }

    public void onSignupSuccess() {
        //registrar dados inseridos
        UserProfile usuario = new UserProfile();
        CreateUserAccount(usuario);
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Falha na criação do cadastro", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        return;
    }

    //region Validação de dados
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
        usuarios.setTipouser(verificaTipoUsuario());
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

        if (usuarios.getMobile().isEmpty() || usuarios.getMobile().length() < 11) {
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
    @Override
    public void onBackPressed() {
        Intent h = new Intent(Cadastro.this, Login.class);
        startActivity(h);
    }

    public String verificaTipoUsuario() {
        return switchTipoUsuario.isChecked() ? "M" : "P";
    }


}
