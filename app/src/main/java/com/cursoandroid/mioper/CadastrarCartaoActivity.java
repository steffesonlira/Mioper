package com.cursoandroid.mioper;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static com.cursoandroid.mioper.UsuarioFirebase.getUsuarioAtual;

public class CadastrarCartaoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText numCartao;
    private EditText dataVencimentoCartao;
    private EditText codNumCartao;
    private Button confirmar;
    private Spinner paisesCadastrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_cartao);

        //ComboBox de nome dos paises
        paisesCadastrados = findViewById(R.id.paisesCadastradosID);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paises, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paisesCadastrados.setAdapter(adapter);
        paisesCadastrados.setOnItemSelectedListener(this);
        //end combobox action

        confirmar = findViewById(R.id.botaoConfirmarCadastroCartaoID);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numCartao = findViewById(R.id.numeroCartaoID);
                dataVencimentoCartao = findViewById(R.id.dataVencimentoCartaoID);
                codNumCartao = findViewById(R.id.codigoSegurancaCartaoID);

                salvarCartaoCadastrado(numCartao.getText().toString());
            }
        });
    }

    public void salvarCartaoCadastrado(String numeroDoCartao) {
        FirebaseUser firebaseUser = getUsuarioAtual();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        String userEmail = firebaseUser.getEmail();
        String userMailReplaced = userEmail.replace('.', '-');

        DatabaseReference metodosPagamentos = firebaseRef.child("CartoesPagamentos").child(userMailReplaced);

        String idUsuarioDoCartao = metodosPagamentos.push().getKey();
        metodosPagamentos.child(idUsuarioDoCartao).setValue(numeroDoCartao);
    }


    public void ValidaCartao(String numeroCartao) {


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String paisSelecionado = parent.getItemAtPosition(position).toString(); //recebe o país selecionado
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}