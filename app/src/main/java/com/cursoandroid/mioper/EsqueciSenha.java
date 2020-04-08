package com.cursoandroid.mioper;

//region IMPORT
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
//endregion

//region CLASSE EsqueciSenha
public class EsqueciSenha extends AppCompatActivity {

    //region declaração de variáveis
    private AutoCompleteTextView email;
    private FirebaseAuth firebaseAuth;
    private Button buttonEsqueciSenha;
    private EditText userEmail;
    //endregion

    //region ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.edtEsqueciSenhaId);
        buttonEsqueciSenha = findViewById(R.id.btnRecuperarSenhaId);

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Retornar ao Login");
        //endregion



        // region click botão esqueci senha
        buttonEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equals("")) {
                    Toast.makeText(EsqueciSenha.this, "Favor inserir o e-mail para redefinição de senha"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                //Enviar ao e-mail cadastrado no Firebase Auth um link com o formulário de recuperação de senha
                firebaseAuth
                        .sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    final ProgressDialog progressDialog = new ProgressDialog(EsqueciSenha.this);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("Enviando e-mail para recuperação de senha...");
                                    progressDialog.show();

                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    progressDialog.dismiss();
                                                }
                                            }, 2500);
                                    email.setText("");


                                } else {
                                    String excecao = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        excecao = "Usuário não está cadastrado.";
                                    } catch (Exception e) {
                                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(EsqueciSenha.this,
                                            excecao,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                //endregion
            }
        });
        //endregion
    }
    //endregion

    //region METODO Criar requisição de Senha esquecida
    @Override
    protected void onResume() {
        super.onResume();
    }
    //endregion

    //region METODO onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal
                , menu);
        return true;
    }
    //endregion

    //region METODO onOptionsItemSelected
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