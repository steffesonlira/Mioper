package com.cursoandroid.mioper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class cadastrarCartao extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText numCartao;
    private EditText dataVencimentoCartao;
    private EditText codNumCartao;
    private Button confirmar;
    private Spinner paisesCadastrados;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_cadastrar_cartao);

        confirmar = findViewById(R.id.confirmaCadastroId);

        Cartao cartao = new Cartao();

        //ComboBox de nome dos paises
        paisesCadastrados = findViewById(R.id.paisesId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paises, android.R.layout.simple_spinner_item);
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

                validate();

                Cartao cartao = new Cartao();
                cartao.setNumeroCartao(numCartao.getText().toString());

                salvarCartao(cartao);
            }
        });


    }

    //region lista de paises
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String paisSelecionado = parent.getItemAtPosition(position).toString(); //recebe o país selecionado
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //end region lista de paises

    private void salvarCartao(Cartao cartao){
        //o código daqui pra baixo pode ser utilizado para inserir qualquer dado no banco
        String email = UsuarioFirebase.getIdentificadorUsuario();        //coloca o email como indentificador
        String emailID = email.replace(".","-");    //faz um replace no email identificador para buscar o ID

        HashMap<Object, String> hashMap = new HashMap<>();//declara o hashMpa
        hashMap.put("numeroCartao",cartao.getNumeroCartao()); //insere o nome da informação no hashMap e o valor da variavel

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();//classe para iniciar o database
        DatabaseReference cadastroDeCartao = firebaseRef.child( "Users" ).child( emailID ).child("MétodosDePagamentos");//caminho que sera salvo o dado

        cadastroDeCartao.setValue(hashMap);//insere o conteudo do hashMap no banco a partir do caminho para inserir o dado


        //final da inserção no banco
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
//        String dateFormat = "MM/uu";
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat).withResolverStyle(ResolverStyle.STRICT);
//        LocalDate date = LocalDate.parse(dataCartaoValidacao, dateTimeFormatter);
//
//        if (dataCartaoValidacao.isEmpty()) {
//            dataVencimentoCartao.setError("Entre com a data");
//            valid = false;
//        } else if (date.equals(dataVencimentoCartao)){
//               dataVencimentoCartao.setError(null);
//        }else{
//            dataVencimentoCartao.setError("Data inválida");
//            valid = false;
//        }

        //validacao do codigo de seguranca da cartao
        if (codNumCartaoValidacao.isEmpty() || codNumCartaoValidacao.length() < 3 ) {
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
