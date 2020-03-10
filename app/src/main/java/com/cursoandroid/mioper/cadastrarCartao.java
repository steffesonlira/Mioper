package com.cursoandroid.mioper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class cadastrarCartao extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText numCartao;
    private EditText dataVencimentoCartao;
    private EditText codNumCartao;
    private Button confirmar;
    private Spinner paisesCadastrados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_cadastrar_cartao);

        confirmar = findViewById(R.id.confirmaCadastroId);


        //ComboBox de nome dos paises
        paisesCadastrados = findViewById(R.id.paisesId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paises,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paisesCadastrados.setAdapter(adapter);
        paisesCadastrados.setOnItemSelectedListener(this);
        //end combobox action

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recebe os numeros digitados
                numCartao = findViewById(R.id.numeroCartaoId);
                dataVencimentoCartao = findViewById(R.id.dataVencCartaoId);
                codNumCartao = findViewById(R.id.codigoCartaoId);

                Toast.makeText(getApplicationContext(), numCartao.getText().toString(), Toast.LENGTH_LONG);
                validate();


            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String paisSelecionado = parent.getItemAtPosition(position).toString(); //recebe o país selecionado
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean validate() {
        boolean valid = true;

        String numCartaoValidacao = numCartao.getText().toString();
        String dataCartaoValidacao = dataVencimentoCartao.getText().toString();
        String codNumCartaoValidacao = codNumCartao.getText().toString();

        //validacao do campo de numero do cartao
        if (numCartaoValidacao.isEmpty() || numCartaoValidacao.length() < 12 ) {
            numCartao.setError("Entre com pelo menos 12 caracteres");
            valid = false;
        } else if (numCartaoValidacao.equals("111111111111111")){
            numCartao.setError("Número de Cartão inválido");
            valid = false;
        }else{
            numCartao.setError(null);
        }

        //validacao do campo da data de vencimento do cartao
        String dateFormat = "MM/uu";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat).withResolverStyle(ResolverStyle.STRICT);
        LocalDate date = LocalDate.parse(dataCartaoValidacao, dateTimeFormatter);

        if (dataCartaoValidacao.isEmpty()) {
            dataVencimentoCartao.setError("Entre com a data");
            valid = false;
        } else if (date.equals(dataVencimentoCartao)){
               dataVencimentoCartao.setError(null);
        }else{
            dataVencimentoCartao.setError("Data inválida");
            valid = false;
        }

        //validacao do codigo de seguranca da cartao
        if (codNumCartaoValidacao.isEmpty() || codNumCartaoValidacao.length() < 3 || codNumCartaoValidacao.length() > 3 ) {
            numCartao.setError("Entre com o código do cartão corretamente");
            valid = false;
        } else if (numCartaoValidacao.equals("111")){
            numCartao.setError("Número de Cartão inválido");
            valid = false;
        }else{
            numCartao.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {

        Intent h= new Intent(cadastrarCartao.this,GerenciarPagamentos.class);
        startActivity(h);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, GerenciarPagamentos.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }
}
