package com.cursoandroid.mioper;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

        //RECEBE DADOS DA TELA DE MEUS DADOS
        Bundle dados = getIntent().getExtras();
        userID = dados.getString("idUsuario");
        senha = dados.getString("senha");
        email = dados.getString("email");
    }

    //ALTERA A SENHA DO USUÁRIO NO FIREBASE
    public void AlterarSenha(final View view) {

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

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Aviso!")
                        .setMessage("Para sua segurança o processo de reset de senha é realizado em duas etapas. Verifique seu e-mail!")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();



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
}
