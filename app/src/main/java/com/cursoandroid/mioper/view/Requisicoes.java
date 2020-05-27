package com.cursoandroid.mioper.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cursoandroid.mioper.R;
import com.cursoandroid.mioper.firebase.UsuarioFirebase;
import com.cursoandroid.mioper.adapter.RecyclerItemClickListener;
import com.cursoandroid.mioper.adapter.RequisicoesAdapter;
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase;
import com.cursoandroid.mioper.model.Requisicao;
import com.cursoandroid.mioper.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.cursoandroid.mioper.firebase.UsuarioFirebase.getIdentificadorUsuario;
import static com.cursoandroid.mioper.firebase.UsuarioFirebase.getUsuarioAtual;

public class Requisicoes extends AppCompatActivity {

    //Componentes
    private RecyclerView recyclerRequisicoes;
    private TextView textResultado;

    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private UserProfile motorista;
    DataSnapshot _dataSnapshot;

    String nomeUsuario1;
    String celularUsuario;
    String senhaUsuario;
    String emailUsuario;
    String enderecoUsuario;
    String nascimentoUsuario;
    String cpfUsuario;
    String generoUsuario;
    String tipoUsuario;


    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);


        //FAZ CONSULTA NO FIREBASE PARA PASSAR OS DADOS DO MOTORISTA PARA TELA DE MEUS DADOS
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

                    _dataSnapshot = dataSnapshot;

                    nomeUsuario1 = usuario.getName();
                    celularUsuario = usuario.getMobile();
                    senhaUsuario = usuario.getSenha();
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


        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Requisições");
        //endregion

        inicializarComponentes();

        //Recuperar localizacao do usuário
        recuperarLocalizacaoUsuario();
    }

    @Override
    protected void onStart() {
        super.onStart();

        verificaStatusRequisicao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inicializarComponentes();
        //Recuperar localizacao do usuário
        recuperarLocalizacaoUsuario();
    }

    private void verificaStatusRequisicao() {

        UserProfile usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        Query requisicoesPesquisa = requisicoes.orderByChild("motorista/id")
                .equalTo(usuarioLogado.getId());

        requisicoesPesquisa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Requisicao requisicao = ds.getValue(Requisicao.class);

                    if (requisicao.getStatus().equals(Requisicao.STATUS_A_CAMINHO)
                            || requisicao.getStatus().equals(Requisicao.STATUS_VIAGEM)
                            || requisicao.getStatus().equals(Requisicao.STATUS_FINALIZADA)) {
                        motorista = requisicao.getMotorista();
                        abrirTelaCorrida(requisicao.getId(), motorista, true);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                //Atualizar GeoFire
                UsuarioFirebase.atualizarDadosLocalizacao(
                        location.getLatitude(),
                        location.getLongitude()
                );

                if (!latitude.isEmpty() && !longitude.isEmpty()) {
                    motorista.setLatitude(latitude);
                    motorista.setLongitude(longitude);

                    adicionaEventoCliqueRecyclerView();
                    locationManager.removeUpdates(locationListener);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Solicitar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void abrirTelaCorrida(String idRequisicao, UserProfile motorista, boolean requisicaoAtiva) {
        Intent i = new Intent(Requisicoes.this, Corrida.class);
        i.putExtra("idRequisicao", idRequisicao);
        i.putExtra("motorista", motorista);
        i.putExtra("requisicaoAtiva", requisicaoAtiva);
        startActivity(i);
    }

    private void inicializarComponentes() {

        // getSupportActionBar().setTitle("Requisições");

        //Configura componentes
        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
        textResultado = findViewById(R.id.textResultado);

        //Configurações iniciais
        motorista = UsuarioFirebase.getDadosUsuarioLogado();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //Configurar RecyclerView
        adapter = new RequisicoesAdapter(listaRequisicoes, getApplicationContext(), motorista);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRequisicoes.setLayoutManager(layoutManager);
        recyclerRequisicoes.setHasFixedSize(true);
        recyclerRequisicoes.setAdapter(adapter);

        recuperarRequisicoes();

    }

    private void adicionaEventoCliqueRecyclerView() {

        //Adiciona evento de clique no recycler
        recyclerRequisicoes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerRequisicoes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Requisicao requisicao = listaRequisicoes.get(position);
                                abrirTelaCorrida(requisicao.getId(), motorista, false);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void recuperarRequisicoes() {

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        Query requisicaoPesquisa = requisicoes.orderByChild("status")
                .equalTo(Requisicao.STATUS_AGUARDANDO);

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    textResultado.setVisibility(View.GONE);
                    recyclerRequisicoes.setVisibility(View.VISIBLE);
                } else {
                    textResultado.setVisibility(View.VISIBLE);
                    recyclerRequisicoes.setVisibility(View.GONE);
                }

                listaRequisicoes.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Requisicao requisicao = ds.getValue(Requisicao.class);
                    listaRequisicoes.add(requisicao);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //region onOptionsItemSelected() Ao clicar na seta voltar do toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar

                AlertDialog.Builder builder2 = new AlertDialog.Builder(Requisicoes.this, R.style.AlertDialogTheme);
                View view2 = LayoutInflater.from(Requisicoes.this).inflate(R.layout.layout_success_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccess));
                builder2.setView(view2);
                ((TextView) view2.findViewById(R.id.textTitleSuccess)).setText(getResources().getString(R.string.warning_title_requisicao));
                ((TextView) view2.findViewById(R.id.textMessageSuccess)).setText(getResources().getString(R.string.text_desc_requisicao));
                ((Button) view2.findViewById(R.id.buttonConfirmaSuccess)).setText(getResources().getString(R.string.confirmar));
                ((Button) view2.findViewById(R.id.buttonCancelSuccess)).setText(getResources().getString(R.string.cancelar));
                ((ImageView) view2.findViewById(R.id.imageIconSuccess)).setImageResource(R.drawable.logo);

                final AlertDialog alertDialog = builder2.create();

                view2.findViewById(R.id.buttonConfirmaSuccess).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        alertDialog.dismiss();
                        autenticacao.signOut();
                        Intent i = new Intent(Requisicoes.this, Login.class);
                        finishAffinity();
                        startActivity(i);

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
                break;
            case R.id.menuSair:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(Requisicoes.this, R.style.AlertDialogTheme);
                View view3 = LayoutInflater.from(Requisicoes.this).inflate(R.layout.layout_success_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccess));
                builder3.setView(view3);
                ((TextView) view3.findViewById(R.id.textTitleSuccess)).setText(getResources().getString(R.string.warning_title_requisicao));
                ((TextView) view3.findViewById(R.id.textMessageSuccess)).setText(getResources().getString(R.string.text_desc_requisicao));
                ((Button) view3.findViewById(R.id.buttonConfirmaSuccess)).setText(getResources().getString(R.string.confirmar));
                ((Button) view3.findViewById(R.id.buttonCancelSuccess)).setText(getResources().getString(R.string.cancelar));
                ((ImageView) view3.findViewById(R.id.imageIconSuccess)).setImageResource(R.drawable.logo);

                final AlertDialog alertDialogSair = builder3.create();

                view3.findViewById(R.id.buttonConfirmaSuccess).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        alertDialogSair.dismiss();
                        autenticacao.signOut();
                        Intent i = new Intent(Requisicoes.this, Login.class);
                        startActivity(i);
                        finishAffinity();

                    }
                });

                view3.findViewById(R.id.buttonCancelSuccess).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogSair.dismiss();
                    }
                });
                if (alertDialogSair.getWindow() != null) {
                    alertDialogSair.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialogSair.show();
                break;
            case R.id.gerenciarDados:
                try {
                    //SE NÃO EXISTIR DADOS DO MOTORISTA
                    if (!_dataSnapshot.exists()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Requisicoes.this, R.style.AlertDialogTheme);
                        View view4 = LayoutInflater.from(Requisicoes.this).inflate(R.layout.layout_successok_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccessOk));
                        builder.setView(view4);
                        ((TextView) view4.findViewById(R.id.textTitleSuccessOk)).setText(getResources().getString(R.string.success_title_nao_motorista));
                        ((TextView) view4.findViewById(R.id.textMessageSuccessOk)).setText(getResources().getString(R.string.text_desc_nao_motorista));
                        ((Button) view4.findViewById(R.id.buttonSuccessOk)).setText(getResources().getString(R.string.confirmar));
                        ((ImageView) view4.findViewById(R.id.imageIconSuccessOk)).setImageResource(R.drawable.logo);

                        final AlertDialog alertDialog2 = builder.create();

                        view4.findViewById(R.id.buttonSuccessOk).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog2.dismiss();
                            }
                        });
                        if (alertDialog2.getWindow() != null) {
                            alertDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        }
                        alertDialog2.show();
                        break;
                    } else {

                        Intent i = new Intent(Requisicoes.this, MeusDados.class);
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

                    }

                } catch (Exception e) {
                    Toast.makeText(Requisicoes.this,
                            "Por Favor realize a operação novamente!",
                            Toast.LENGTH_SHORT).show();
                }

        }
        return true;
    }

    //BOTÃO INFERIOR ESQUERDO DE VOLTAR DO SISTEMA ANDROID
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(Requisicoes.this, R.style.AlertDialogTheme);
        View view2 = LayoutInflater.from(Requisicoes.this).inflate(R.layout.layout_success_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccess));
        builder2.setView(view2);
        ((TextView) view2.findViewById(R.id.textTitleSuccess)).setText(getResources().getString(R.string.warning_title_requisicao));
        ((TextView) view2.findViewById(R.id.textMessageSuccess)).setText(getResources().getString(R.string.text_desc_requisicao));
        ((Button) view2.findViewById(R.id.buttonConfirmaSuccess)).setText(getResources().getString(R.string.confirmar));
        ((Button) view2.findViewById(R.id.buttonCancelSuccess)).setText(getResources().getString(R.string.cancelar));
        ((ImageView) view2.findViewById(R.id.imageIconSuccess)).setImageResource(R.drawable.logo);

        final AlertDialog alertDialog = builder2.create();

        view2.findViewById(R.id.buttonConfirmaSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                alertDialog.dismiss();
                autenticacao.signOut();
                Intent i = new Intent(Requisicoes.this, Login.class);
                finishAffinity();
                startActivity(i);

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

}
