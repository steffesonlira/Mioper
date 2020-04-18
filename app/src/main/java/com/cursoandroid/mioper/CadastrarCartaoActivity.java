package com.cursoandroid.mioper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Cadastro de Cartões");
        //endregion

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("confirmação")
                .setMessage("Deseja confirmar a operação?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser firebaseUser = getUsuarioAtual();
                        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

                        String userEmail = firebaseUser.getEmail();
                        String userMailReplaced = userEmail.replace('.', '-');

                        DatabaseReference metodosPagamentos = firebaseRef.child("CartoesPagamentos").child(userMailReplaced);

                        String idUsuarioDoCartao = metodosPagamentos.push().getKey();
                        metodosPagamentos.child(idUsuarioDoCartao).setValue(numeroDoCartao);

                        Toast.makeText(CadastrarCartaoActivity.this,
                                "Cartão cadastrado com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        MudarTelaAoSalvar();
                    }
                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void ValidaCartao(String numeroCartao) {


    }

    public void MudarTelaAoSalvar(){
        Intent intent = new Intent(this,MetodosDePagamentoActivity.class);
        startActivity(intent);//retorna para a tela de metodos de pagamentos cadastrados
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String paisSelecionado = parent.getItemAtPosition(position).toString(); //recebe o país selecionado
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}