package com.cursoandroid.mioper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.cursoandroid.mioper.UsuarioFirebase.getIdentificadorUsuario;
import static com.cursoandroid.mioper.UsuarioFirebase.getUsuarioAtual;

public class MetodosDePagamentoActivity extends AppCompatActivity {
    public Button botaoCadastrarCartao;
    public String userMailReplaced;

    private  ArrayList<String> metodosOk = new ArrayList<>();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodos_de_pagamento);
        resgatarMetodosPagamentos();

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Métodos de Pagamento");
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
        Intent intent = new Intent(this,CadastrarCartaoActivity.class);
        startActivity(intent);
    }

    public void resgatarMetodosPagamentos(){
        FirebaseUser user = getUsuarioAtual();
        String userEmail = user.getEmail();

        userMailReplaced = userEmail.replace('.', '-');

        if (user != null) {
            Log.d("resultado", "onDataChange: " + getIdentificadorUsuario());
            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("CartoesPagamentos").child(userMailReplaced);

            usuariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //ADICIONA O OBJETO HISTORICO COM TODOS OS DADOS NO ARRAY
                        metodosOk.add(ds.getValue().toString());
                        IniciarRecyclerView(metodosOk);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }



    public void IniciarRecyclerView(ArrayList metodos){

        AdapterMetodosPagamentos adapter = new AdapterMetodosPagamentos(metodos);

        RecyclerView recyclerView = findViewById(R.id.listaMetodosPagamentosID);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyItemRemoved(adapter.getItemCount());


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
                finish();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }
}
