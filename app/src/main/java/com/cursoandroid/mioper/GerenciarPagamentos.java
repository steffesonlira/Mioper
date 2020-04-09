package com.cursoandroid.mioper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GerenciarPagamentos extends AppCompatActivity{
    public Button botaoCadastrarCartao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_gerenciar_pagamentos);

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Gerenciar Pagamentos");
        //endregion

        botaoCadastrarCartao = findViewById(R.id.adcionarCartaoID);
        botaoCadastrarCartao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarCartao();
            }
        });

    }

    public void adicionarCartao(){
        Intent intent = new Intent(this,CadastrarCartao.class);
        startActivity(intent);
    }






    /*------------------------metodos padroes------------------------*/
    @Override
    public void onBackPressed() {
        Intent h= new Intent(GerenciarPagamentos.this,Principal.class);
        startActivity(h);
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
                startActivity(new Intent(this, Principal.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }


}
