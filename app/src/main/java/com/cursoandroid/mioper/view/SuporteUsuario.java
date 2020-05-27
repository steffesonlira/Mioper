package com.cursoandroid.mioper.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cursoandroid.mioper.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SuporteUsuario extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;
    private EditText txtPara, txtMensagem;
    private Spinner assunto;
    private Button btnMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suporteusuario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();

        //ESCONDE O TECLADO AO INICIAR A ACTIVITY
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //ComboBox de nome dos paises
        txtPara = findViewById(R.id.idPara);
        txtMensagem = findViewById(R.id.idMensagem);
        btnMensagem = findViewById(R.id.btnEnviarMensagem);
        assunto = findViewById(R.id.idAssunto);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assunto, R.layout.simple_custom_spinner2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assunto.setAdapter(adapter);
        assunto.setOnItemSelectedListener(this);
        //end combobox action

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Suporte ao Usuário");
        //endregion


        btnMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("mailto:"));

                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{txtPara.getText().toString()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,assunto.getSelectedItem().toString());
                emailIntent.putExtra(Intent.EXTRA_TEXT,txtMensagem.getText().toString());
                emailIntent.setSelector(intent);
                startActivity(Intent.createChooser(emailIntent, "Enviar E-mail via..."));
                txtMensagem.setText("");

            }
        });
    }




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
                finish();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
