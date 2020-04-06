package com.cursoandroid.mioper;

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

public class Principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Declaração de variáveis
    private FragmentManager fragmentManager;
    DrawerLayout drawer;
    TextView nomeUsuario;
    String nomeUsuario1;
    String celularUsuario;
    String senhaUsuario;
    String repitasenha;
    String emailUsuario;
    String enderecoUsuario;
    String nascimentoUsuario;
    String cpfUsuario;
    String generoUsuario;
    Principal activity;
    String tipoUsuario;
    String usuario;

    NavigationView navigationView;
    private static final String TAG = "Principal";
    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //RECEBE DADOS DA TELA DE LOGIN
        nomeUsuario = findViewById(R.id.nomeUsuário);
        Bundle dados = getIntent().getExtras();
        if (dados != null) {
            usuario = dados.getString("name");

        }


        //configure navigation bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();


        //COLOCAR NOME DO USUÁRIO NO NAV HEADER
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nomeUsuário);
        navUsername.setText(usuario);

        //each new menu item must be added here
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_data, R.id.nav_payment, R.id.nav_historico,
                R.id.nav_indication, R.id.nav_game, R.id.nav_about_us, R.id.nav_exit)
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

        /*
//        //RECEBE DADOS DA TELA DE LOGIN
        nomeUsuario = findViewById(R.id.nomeUsuário);
        Bundle dados = getIntent().getExtras();
        String usuario = dados.getString("name");
//
        //COLOCAR NOME DO USUÁRIO NO NAV HEADER
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nomeUsuário);
        navUsername.setText(usuario);
*/

        //FAZ CONSULTA NO FIREBASE
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    //CHAMA TELAS DE ACORDO COM O ITEM DE MENU  CLICADO

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.nav_home:
                Intent m = new Intent(Principal.this, Passageiro.class);
                startActivity(m);
                break;
            case R.id.nav_data:

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
            case R.id.nav_payment:
                Intent g = new Intent(Principal.this, GerenciarPagamentos.class);
                startActivity(g);
                break;
            case R.id.nav_historico:
                Intent s = new Intent(Principal.this, HistoricoViagens.class);
                startActivity(s);
            case R.id.nav_indication:
                //     Intent t = new Intent(Principal.this, IndiqueGanhe.class);
                //   startActivity(t);
                break;
            case R.id.nav_game:
                Intent u = new Intent(Principal.this, SuporteUsuario.class);
                startActivity(u);
                break;
            case R.id.nav_about_us:
                Intent v = new Intent(Principal.this, Sobre.class);
                startActivity(v);
                break;
            case R.id.nav_exit:

                if (item.getItemId() == R.id.nav_exit) {

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

}

