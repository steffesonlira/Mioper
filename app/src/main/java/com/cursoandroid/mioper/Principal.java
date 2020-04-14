package com.cursoandroid.mioper;

//region IMPORT

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.cursoandroid.mioper.UsuarioFirebase.getIdentificadorUsuario;
import static com.cursoandroid.mioper.UsuarioFirebase.getUsuarioAtual;
//endregion

public class Principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //region Declaração de variáveis
    private FragmentManager fragmentManager;
    DrawerLayout drawer;
    TextView nomeUsuario;
    static String nomeUsuario1;
    static String celularUsuario;
    static String senhaUsuario;
    static String repitasenha;
    static String emailUsuario;
    static String enderecoUsuario;
    static String nascimentoUsuario;
    static String cpfUsuario;
    static String generoUsuario;
    Principal activity;
    static String tipoUsuario;
    static String usuario;
    NavigationView navigationView;
    private static final String TAG = "Principal";
    private AppBarConfiguration mAppBarConfiguration;
    //endregion
    
    //region onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //region RECEBE DADOS DA TELA DE LOGIN
        nomeUsuario = findViewById(R.id.nomeUsuário);
        Bundle dados = getIntent().getExtras();
        if (dados != null) {
            usuario = dados.getString("name");

        }
        //endregion

        //region configure navigation bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        //endregion

        //region COLOCAR NOME DO USUÁRIO NO NAV HEADER
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nomeUsuário);
        navUsername.setText(usuario);
        //endregion

        //region Menu Item
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.sair, R.id.nav_dados, R.id.nav_pagamento, R.id.nav_historico,
                R.id.nav_indicacao, R.id.nav_suporte, R.id.nav_sobre, R.id.nav_sair)
                .setDrawerLayout(drawer)
                .build();

        nomeUsuario = findViewById(R.id.nomeUsuário);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.commitNowAllowingStateLoss();
        //endregion


        //region FAZ CONSULTA NO FIREBASE
        FirebaseUser user = getUsuarioAtual();
        if (user != null) {
            Log.d("resultado", "onDataChange: " + getIdentificadorUsuario());
            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("Users")
                    .child(getIdentificadorUsuario());
            System.out.println(usuariosRef);
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("resultado", "onDataChange: " + dataSnapshot.toString());
                    UserProfile usuario = dataSnapshot.getValue(UserProfile.class);
                    nomeUsuario1 = usuario.getName();
                    celularUsuario = usuario.getMobile();
                    senhaUsuario = usuario.getSenha();
                    repitasenha = usuario.getRepitasenha();
                    emailUsuario = usuario.getEmail();
                    enderecoUsuario = usuario.getAdress();
                    nascimentoUsuario = usuario.getNascimento();
                    cpfUsuario = usuario.getCpf();
                    generoUsuario = usuario.getGenero();
                    tipoUsuario = usuario.getTipouser();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
        //endregion

    }
    //endregion

    //region METODO onCreateMenu()
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }
    //endregion

    //region CHAMA TELAS DE ACORDO COM O ITEM DE MENU  CLICADO onNavigationItemSelected()
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sair:
                Intent m = new Intent(Principal.this, Passageiro.class);
                startActivity(m);
                break;
            case R.id.nav_dados:
                Intent i = new Intent(Principal.this, MeusDados.class);
                //CHAMA A TELA MEUS DADOS E PASSA OS DADOS
                i.putExtra("name", nomeUsuario1);
                i.putExtra("mobile", celularUsuario);
                i.putExtra("senha", senhaUsuario);
                i.putExtra("email", emailUsuario);
                i.putExtra("adress", enderecoUsuario);
                i.putExtra("nascimento", nascimentoUsuario);
                i.putExtra("cpf", cpfUsuario);
                i.putExtra("genero", generoUsuario);
                i.putExtra("tipouser", tipoUsuario);
                startActivity(i);
                break;
            case R.id.nav_pagamento:
                Intent g = new Intent(Principal.this, MetodosDePagamentoActivity.class);
                startActivity(g);
                break;
            case R.id.nav_historico:
                Intent s = new Intent(Principal.this, HistoricoViagens.class);
                startActivity(s);
                break;
            case R.id.nav_indicacao:
                Intent t = new Intent(Principal.this, IndiqueGanhe.class);
                startActivity(t);
                break;
            case R.id.nav_suporte:
                Intent u = new Intent(Principal.this, SuporteUsuario.class);
                startActivity(u);
                break;
            case R.id.nav_sobre:
                Intent v = new Intent(Principal.this, Sobre.class);
                startActivity(v);
                break;
            case R.id.nav_sair:

                if (item.getItemId() == R.id.nav_sair) {
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
    //endregion

}

