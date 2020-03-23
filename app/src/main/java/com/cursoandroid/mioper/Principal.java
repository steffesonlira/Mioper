package com.cursoandroid.mioper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Declaração de variáveis
    private FragmentManager fragmentManager;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    private static final String TAG = "Principal";
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.add(R.id.fragment_container, new MapaFragment(), "MapaFragment");
        transaction.commitNowAllowingStateLoss();
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

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    private void showFragment(Fragment fragment , String name)
    {
        FragmentTransaction transaction;
        transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, fragment, name);
        transaction.commit();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){

            case R.id.nav_home:
               // showFragment(new MapaFragment(), "MapsFragment");
                //Intent h= new Intent(Principal.this,Principal.class);
                //startActivity(h);
                break;
            case R.id.nav_data:
                Intent i= new Intent(Principal.this,MeusDados.class);
                startActivity(i);
                break;
            case R.id.nav_payment:
                Intent g= new Intent(Principal.this,GerenciarPagamentos.class);
                startActivity(g);
                break;
            case R.id.nav_travel_history:
                Intent s= new Intent(Principal.this,HistoricoViagens.class);
                startActivity(s);
            case R.id.nav_indication:
                Intent t= new Intent(Principal.this,IndiqueGanhe.class);
                startActivity(t);
                break;
            case R.id.nav_game:
                Intent u = new Intent(Principal.this, SuporteUsuario.class);
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

    private void setupFirebaseListener(){
        Log.d(TAG, "setupFirebaseListener: listener up the auth state listener.");
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged signed_in: " + user.getUid());
                }else{
                    Log.d(TAG, "onAuthStateChanged signed_out: ");
                    Intent intent = new Intent(Principal.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

    }
}
