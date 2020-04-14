package com.cursoandroid.mioper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Requisicoes extends AppCompatActivity {

    //Componentes
    private RecyclerView recyclerRequisicoes;
    private TextView textResultado;

    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private UserProfile motorista;

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
    protected void onResume() {
        super.onResume();
        //RECEDE DADOS SEMPRE QUE A ACTIVITY É RECARREGADA
        nomeUsuario1 = Principal.nomeUsuario1;
        celularUsuario = Principal.celularUsuario;
        senhaUsuario = Principal.senhaUsuario;
        emailUsuario = Principal.emailUsuario;
        enderecoUsuario = Principal.enderecoUsuario;
        nascimentoUsuario = Principal.enderecoUsuario;
        cpfUsuario = Principal.cpfUsuario;
        generoUsuario = Principal.generoUsuario;
        tipoUsuario = Principal.tipoUsuario;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        //RECEDE DADOS DO MOTORISTA DA CLASSE PRINCIPAL
         nomeUsuario1 = Principal.nomeUsuario1;
         celularUsuario = Principal.celularUsuario;
         senhaUsuario = Principal.senhaUsuario;
         emailUsuario = Principal.emailUsuario;
         enderecoUsuario = Principal.enderecoUsuario;
         nascimentoUsuario = Principal.enderecoUsuario;
         cpfUsuario = Principal.cpfUsuario;
         generoUsuario = Principal.generoUsuario;
         tipoUsuario = Principal.tipoUsuario;


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
                autenticacao.signOut();
                startActivity(new Intent(this, Login.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            case R.id.menuSair:
                autenticacao.signOut();
                startActivity(new Intent(this, Login.class));
                finishAffinity();
                break;
            case R.id.gerenciarDados:
                //CHAMA A TELA MEUS DADOS E PASSA OS DADOS
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
        return true;
    }

    //BOTÃO INFERIOR ESQUERDO DE VOLTAR DO SISTEMA ANDROID
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        autenticacao.signOut();
        startActivity(new Intent(this, Login.class));
        finishAffinity();

    }

}
