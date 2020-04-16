package com.cursoandroid.mioper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Sobre extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button btn_manifesto, btn_design, btn_historia, btn_missao, btn_visao, btn_valores;
    View viewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        mAuth = FirebaseAuth.getInstance();
        btn_manifesto = findViewById(R.id.btnManifesto);
        btn_design = findViewById(R.id.btnDesignThinking);
        btn_historia = findViewById(R.id.btnHistoria);
        btn_missao = findViewById(R.id.btnMissao);
        btn_visao = findViewById(R.id.btnVisao);
        btn_valores = findViewById(R.id.btnValores);
        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.customtoast, (ViewGroup) findViewById(R.id.custom_layout));

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Sobre o Mioper");
        //endregion


        //click buttons
        btn_manifesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sobre.this, Onboarding.class);
                startActivity(intent);
            }
        });
        btn_design.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDesign = new Intent(Sobre.this, DesignThinking.class);
                startActivity(intentDesign);
            }
        });
        btn_historia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDesign = new Intent(Sobre.this, Historia.class);
                startActivity(intentDesign);

            }
        });
        btn_missao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMissao = new Intent(Sobre.this, Missao.class);
                startActivity(intentMissao);
            }
        });
        btn_visao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisao = new Intent(Sobre.this, Visao.class);
                startActivity(intentVisao);

            }
        });
        btn_valores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentValores = new Intent(Sobre.this, Valores.class);
                startActivity(intentValores);

            }
        });
    }


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


}
