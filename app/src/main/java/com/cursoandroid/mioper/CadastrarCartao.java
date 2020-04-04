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

public class CadastrarCartao extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paises,android.R.layout.simple_spinner_item);
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

//        int soma = 0;
//        String numCartao = numeroCartao;
//        String numString;
//
//        //Cartão com seqüências de caracteres menor ou igual a 15 dígitos
//        if (numCartao.length() <= 15) {
//            for (int i = 0; i <= numCartao.length(); i++) {
//                numString = (numCartao.substring(i, i + 1));
//
//                if (i % 2 == 0) {
//                    soma += (Integer.parseInt(numString) * 1);
//                } else {
//                    if ((Integer.parseInt(numString) * 2) > 9) {
//                        soma += ((Integer.parseInt(numString) * 2) - 9);
//                    } else {
//                        soma += ((Integer.parseInt(numString) * 2));
//                    }
//                }
//            }
//        }
//
//        //Cartão com seqüências de caracteres maior ou igual a 16 dígitos
//        if(numCartao.length()>=16){
//            for(int i = 0; i <=numCartao.length();i++){
//                numString = (numCartao.substring(i,i+1));
//                if (i%2 == 0){
//                    if((Integer.parseInt(numString)*2)>9){
//                        soma += ((Integer.parseInt(numString) * 2) - 9);
//                    }else{
//                        soma +=((Integer.parseInt(numString) * 2));
//                    }
//                }else{
//                    soma +=(Integer.parseInt(numString) * 1);
//                }
//            }
//        }
//        if (soma % 10 == 0){
//            Toast.makeText(this, "Cartão Cadastrado",Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(this, "Cartão Invalido",Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String paisSelecionado = parent.getItemAtPosition(position).toString(); //recebe o país selecionado
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}