package com.cursoandroid.mioper.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase;
import com.cursoandroid.mioper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class AlterarSenha extends AppCompatActivity {

    EditText senhaAntiga;
    EditText senhaNova;
    EditText confirmarSenha;
    String userID;
    String senha;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);

        senhaAntiga = findViewById(R.id.txtSenhaAntiga);
        senhaNova = findViewById(R.id.txtSenhaNova);
        confirmarSenha = findViewById(R.id.txtConfirmarSenha);

        senhaAntiga.requestFocus();

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Alteração de Senha");
        //endregion

        //RECEBE DADOS DA TELA DE MEUS DADOS
        Bundle dados = getIntent().getExtras();
        userID = dados.getString("idUsuario");
        senha = dados.getString("senha");
        email = dados.getString("email");
    }

    //BOTÃO ALTERAR A SENHA
    public void AlterarSenha(final View view) {


        if (isOnline(this) == false) {
            Toast.makeText(this,
                    "Erro ao alterar sua senha. Verifique sua conexão com a internet.",
                    Toast.LENGTH_SHORT).show();
            return;
        }



        //ESCONDER O TECLADO AO CLICAR NO BOTÃO ALTERAR SENHA
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        //PEGA OS VALORES DOS TEXTVIEW
        String _SenhaAntiga = String.valueOf(senhaAntiga.getText());
        String _SenhaNova = String.valueOf(senhaNova.getText());
        String _ConfirmarSenha = String.valueOf(confirmarSenha.getText());

        if (_SenhaAntiga.isEmpty()) {
            Toast.makeText(AlterarSenha.this,
                    "Digite a senha antiga!",
                    Toast.LENGTH_SHORT).show();
            return;

        }

        if (_SenhaNova.isEmpty()) {
            Toast.makeText(AlterarSenha.this,
                    "Digite a senha nova!",
                    Toast.LENGTH_SHORT).show();
            return;

        }

        if (_ConfirmarSenha.isEmpty()) {
            Toast.makeText(AlterarSenha.this,
                    "Confirme a senha!",
                    Toast.LENGTH_SHORT).show();
            return;

        }

        if (_SenhaNova.equals(_ConfirmarSenha)) {


            //SE SENHA ANTIGA DIGITADA PELO USUARIO FOR IGUAL A SENHA REGISTRADA - REGISTRA SENHA NOVA NO FIREBASE
            if (_SenhaAntiga.equals(senha)) {


                AlertDialog.Builder builder = new AlertDialog.Builder(AlterarSenha.this, R.style.AlertDialogTheme);
                View view2 = LayoutInflater.from(AlterarSenha.this).inflate(R.layout.layout_success_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccessOk));
                builder.setView(view2);
                ((TextView) view2.findViewById(R.id.textTitleSuccess)).setText(getResources().getString(R.string.warning_title_redefinir_ok_conta));
                ((TextView) view2.findViewById(R.id.textMessageSuccess)).setText(getResources().getString(R.string.text_desc_redefinir_ok_conta));
                ((Button) view2.findViewById(R.id.buttonConfirmaSuccess)).setText(getResources().getString(R.string.confirmar));
                ((Button) view2.findViewById(R.id.buttonCancelSuccess)).setText(getResources().getString(R.string.cancelar));
                ((ImageView) view2.findViewById(R.id.imageIconSuccess)).setImageResource(R.drawable.logo);

                final AlertDialog alertDialog = builder.create();

                view2.findViewById(R.id.buttonConfirmaSuccess).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //ALTERA A SENHA NO FIREBASE DATABASE
                        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
                        DatabaseReference usuario = firebaseRef.child("Users").child(userID).child("senha");
                        String senhaNovaUsuario = String.valueOf(senhaNova.getText());
                        usuario.setValue(senhaNovaUsuario);

                        //ALTERA A SENHA NO FIREBASE AUTHENTICATION (SENHA PARA ACESSAR O APP)
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String emailAddress = email;

                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });


                        alertDialog.dismiss();
                        finish();
                    }
                });

                view2.findViewById(R.id.buttonCancelSuccess).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });


                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();


            } else {
                Toast.makeText(AlterarSenha.this,
                        "Senha antiga invalida!",
                        Toast.LENGTH_SHORT).show();

            }


        } else {

            Toast.makeText(AlterarSenha.this,
                    "Sua nova senha não coincide!",
                    Toast.LENGTH_SHORT).show();

        }


    }


    //region Criação do Menu Toolbar XML
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal
                , menu);
        return true;
    }
    //endregion

    //region onOptionsItemSelected() Ao clicar na seta voltar do toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                //startActivity(new Intent(this, MeusDados.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                finish();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }

    //VERIFICA SE HA CONEXÃO COM INTERNET
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
            return true;
        else
            return false;
    }

}
