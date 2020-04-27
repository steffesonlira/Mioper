package com.cursoandroid.mioper;

//region IMPORT

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import java.util.Calendar;

import static com.cursoandroid.mioper.UsuarioFirebase.getIdentificadorUsuario;
import static com.cursoandroid.mioper.UsuarioFirebase.getUsuarioAtual;
//endregion

public class Principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //region Declaração de variáveis
    private FragmentManager fragmentManager;
    DrawerLayout drawer;
    TextView nomeUsuario;
    String boasVindas_;
    private boolean gpsStatus;
    public static boolean verificaRetorno = false;
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
    View viewLayout;
    NavigationView navigationView;
    private static final String TAG = "Principal";
    private AppBarConfiguration mAppBarConfiguration;
    //endregion


    @Override
    protected void onResume() {
        super.onResume();

        //VERIFICA SE GPS DO CELULAR ESTÁ LIGADO
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //region FAZ CONSULTA NO FIREBASE SEMPRE QUE USUÁRIO RETORNAR DA TELA MEUS DADOS
        if (verificaRetorno == true) {
            FirebaseUser user = getUsuarioAtual();
            if (user != null) {
                Log.d("resultado", "onDataChange: " + getIdentificadorUsuario());
                DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("Users")
                        .child(getIdentificadorUsuario());
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
            verificaRetorno = false;
        }
    }

    //region onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //VERIFICA SE GPS DO CELULAR ESTÁ LIGADO
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gpsStatus == false) {
            alertaGpsDesligado();
        } else {
            //EXIBE TOAST DE BOAS VINDAS
            LayoutInflater layoutInflater = getLayoutInflater();
            viewLayout = layoutInflater.inflate(R.layout.customtoast, (ViewGroup) findViewById(R.id.custom_layout));
            //Toast de Boas vindas
            Toast toastCustom = Toast.makeText(Principal.this, "", Toast.LENGTH_SHORT);
            toastCustom.setGravity(Gravity.CENTER, 0, 0);
            toastCustom.setView(viewLayout);
            toastCustom.show();

        }

        //region RECEBE DADOS DA TELA DE LOGIN
        nomeUsuario = findViewById(R.id.nomeUsuário);
        Bundle dados = getIntent().getExtras();
        if (dados != null) {
            usuario = dados.getString("name");

        }

        //IDENTIFICA SE MANHA TARDE OU NOITE
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            boasVindas_ = "Bom Dia";
        } else if (timeOfDay >= 12 && timeOfDay < 18) {
            boasVindas_ = "Boa Tarde";
        } else if (timeOfDay >= 18 && timeOfDay <= 24) {
            boasVindas_ = "Boa Noite";
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


        //region COLOCAR BOM DIA, BOA TARDE OU BOA NOITE NO TEXTFIELD DO NAVHEADER
        TextView navBoasVindas = (TextView) headerView.findViewById(R.id.txtboasVindas);
        navBoasVindas.setText((CharSequence) boasVindas_);
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
            //TELA DE NAVEGAÇAO
            case R.id.sair:
                if (gpsStatus == false) {
                    alertaGpsDesligado();
                } else {
                    Intent m = new Intent(Principal.this, Passageiro.class);
                    startActivity(m);
                }

                break;
            case R.id.nav_dados:
                if (isOnline(this) == true) {
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
                } else {
                    Toast.makeText(this,
                            "Informações indisponíveis. Verifique sua conexão com a internet",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_pagamento:
                Intent g = new Intent(Principal.this, MetodosDePagamentoActivity.class);
                startActivity(g);
                break;
            case R.id.nav_historico:
                if (isOnline(this) == true) {
                    Intent s = new Intent(Principal.this, HistoricoViagens.class);
                    startActivity(s);
                } else {
                    Toast.makeText(this,
                            "Informações indisponíveis. Verifique sua conexão com a internet",
                            Toast.LENGTH_SHORT).show();
                }
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
                Sair();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion


    //ALERTA CHAMADO CASO A LOCALIZAÇÃO DO CELULAR ESTIVER DESATIVADA
    public void alertaGpsDesligado() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Principal.this, R.style.AlertDialogTheme);
        View view2 = LayoutInflater.from(Principal.this).inflate(R.layout.layout_success_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccess));
        builder.setView(view2);
        ((TextView) view2.findViewById(R.id.textTitleSuccess)).setText(getResources().getString(R.string.success_title_sair_gps_ok));
        ((TextView) view2.findViewById(R.id.textMessageSuccess)).setText(getResources().getString(R.string.text_desc_sair_gps_ok));
        ((Button) view2.findViewById(R.id.buttonConfirmaSuccess)).setText(getResources().getString(R.string.confirmar));
        ((Button) view2.findViewById(R.id.buttonCancelSuccess)).setText(getResources().getString(R.string.cancelar));
        ((ImageView) view2.findViewById(R.id.imageIconSuccess)).setImageResource(R.drawable.logo);

        final AlertDialog alertDialog = builder.create();

        view2.findViewById(R.id.buttonConfirmaSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                alertDialog.dismiss();
                //ABRE A TELA DE CONFIGURAÇÕES GERAIS DO ANDROID
                // startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                //ABRE A TELA DE CONFIGURAÇÕES DE LOCALIZAÇÃO DO ANDROID
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

            }
        });

        view2.findViewById(R.id.buttonCancelSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

    }


    public void Sair() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Principal.this, R.style.AlertDialogTheme);
        View view2 = LayoutInflater.from(Principal.this).inflate(R.layout.layout_success_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccess));
        builder.setView(view2);
        ((TextView) view2.findViewById(R.id.textTitleSuccess)).setText(getResources().getString(R.string.warning_title_sair_ok));
        ((TextView) view2.findViewById(R.id.textMessageSuccess)).setText(getResources().getString(R.string.text_desc_sair_ok));
        ((Button) view2.findViewById(R.id.buttonConfirmaSuccess)).setText(getResources().getString(R.string.confirmar));
        ((Button) view2.findViewById(R.id.buttonCancelSuccess)).setText(getResources().getString(R.string.cancelar));
        ((ImageView) view2.findViewById(R.id.imageIconSuccess)).setImageResource(R.drawable.logo);

        final AlertDialog alertDialog = builder.create();

        view2.findViewById(R.id.buttonConfirmaSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                alertDialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();

            }
        });

        view2.findViewById(R.id.buttonCancelSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

    }


    @Override
    public void onBackPressed() {

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
            return true;
        else
            return false;
    }

}

