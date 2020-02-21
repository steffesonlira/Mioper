package com.cursoandroid.mioper;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.view.MenuItem;

import android.view.Menu;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class IndiqueGanhe extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indique_ganhe);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
                Intent h= new Intent(IndiqueGanhe.this,Principal.class);
                startActivity(h);
                break;
            case R.id.nav_data:
                Intent i= new Intent(IndiqueGanhe.this,MeusDados.class);
                startActivity(i);
                break;
            case R.id.nav_payment:
                Intent g= new Intent(IndiqueGanhe.this,GerenciarPagamentos.class);
                startActivity(g);
                break;
            case R.id.nav_travel_history:
                Intent s= new Intent(IndiqueGanhe.this,HistoricoViagens.class);
                startActivity(s);
            case R.id.nav_indication:
                Intent t= new Intent(IndiqueGanhe.this,IndiqueGanhe.class);
                startActivity(t);
                break;
            case R.id.nav_game:
                Intent u = new Intent(IndiqueGanhe.this, Jogo.class);
                startActivity(u);
                break;
            case R.id.nav_exit:

                if(item.getItemId() == R.id.nav_exit){

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
