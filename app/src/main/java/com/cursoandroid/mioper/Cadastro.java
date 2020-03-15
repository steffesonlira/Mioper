package com.cursoandroid.mioper;

//region IMPORTs
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
//endregion

public class Cadastro extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    ToggleButton toggleButton;
    //region Variáveis cadastro do usuário
    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    private FirebaseAuth mAuth;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        ButterKnife.bind(this);
        toggleButton = findViewById(R.id.tbTipoUser);

        //Inserindo máscara ao campo de digitação Celular
        _mobileText.addTextChangedListener(Mask.mask(_mobileText, Mask.FORMAT_FONE));

        mAuth = FirebaseAuth.getInstance();

        _signupButton = findViewById(R.id.btn_signup);


        //region Click botão cadastrar
        _signupButton.setOnClickListener(new View.OnClickListener()
        {
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
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        //endregion

    }

    //Criar ação do botão motorista e user:
    public void onToggleClick(View v){
        if(toggleButton.isChecked()) {
            Toast.makeText(this, "Motorista", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Cliente", Toast.LENGTH_SHORT).show();
        }
    }

    //region Método para criação de usuário e senha no Firebase pelo método de Authentication
    private void CreateUserAccount(String email,String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    //Processo de criação de conta no Firebase
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Cadastro.this, "Conta criada com sucesso", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(Cadastro.this, "Falha na criação da conta" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
    //endregion

    //Chamando função de validação de campos (Criação de ProgressDialog para redirecionar após realização de cadastro
    public void signup() {
        Log.d(TAG, "Signup");
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

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
    //endregion

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        String email_semreplace = _emailText.getText().toString();
        String email = email_semreplace.replace(".","-");
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String tbTipoUser = toggleButton.getText().toString();

        //region Criando HashMap para criação de database
        HashMap<Object, String> hashMap = new HashMap<>();

        hashMap.put("name",name);
        hashMap.put("adress",address);
        hashMap.put("email",email);
        hashMap.put("mobile",mobile);
        hashMap.put("password",password);
        hashMap.put("reEnterPassword",reEnterPassword);
        if(toggleButton.isChecked())
            hashMap.put("Tipo_User","M");
        else{
            hashMap.put("Tipo_User","P");
        }
        //endregion

        //Criando instancia no DataBase Firebase Realtime
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Atribuindo um relacionamento pai
        DatabaseReference reference = database.getReference("Users");

        //setando a chave primária
        reference.child(email).setValue(hashMap);

    }

    public void onSignupSuccess() {
        //registrar dados inseridos
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        CreateUserAccount(email, password);
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        startActivity(new Intent(Cadastro.this, Principal.class));
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Falha na criação do cadastro", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        return;
    }

    //region Validação de dados
    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Entre com pelo menos 3 caracteres");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (address.isEmpty()) {
            _addressText.setError("Entre com um endereço válido");
            valid = false;
        } else {
            _addressText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Entre com um e-mail válido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()<11) {
            _mobileText.setError("Entre com um número de telefone corretamente");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("A senha deve ser entre 4 e 10 caracteres");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
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
        Intent h= new Intent(Cadastro.this,Login.class);
        startActivity(h);
    }

}
