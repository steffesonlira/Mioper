package com.cursoandroid.mioper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
        setSupportActionBar(toolbar);

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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        btnMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW
                , Uri.parse("mailto:" + txtPara.getText().toString()));
                intent.putExtra(Intent.EXTRA_SUBJECT, assunto.getSelectedItem().toString());
                intent.putExtra(intent.EXTRA_TEXT,txtMensagem.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent h= new Intent(SuporteUsuario.this,Principal.class);
                startActivity(h);
                break;
            case R.id.nav_dados:
                Intent i= new Intent(SuporteUsuario.this,MeusDados.class);
                startActivity(i);
                break;
            case R.id.nav_pagamento:
                Intent g= new Intent(SuporteUsuario.this,GerenciarPagamentos.class);
                startActivity(g);
                break;
            case R.id.nav_historico:
                Intent s= new Intent(SuporteUsuario.this,HistoricoViagens.class);
                startActivity(s);
            case R.id.nav_indicacao:
           //     Intent t= new Intent(SuporteUsuario.this,IndiqueGanhe.class);
             //   startActivity(t);
                break;
            case R.id.nav_suporte:
                Intent u = new Intent(SuporteUsuario.this, SuporteUsuario.class);
                startActivity(u);
                break;
            case R.id.nav_sobre:
                Intent v = new Intent(SuporteUsuario.this, Sobre.class);
                startActivity(v);
                break;
            case R.id.nav_sair:

                if(item.getItemId() == R.id.nav_sair){

                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
