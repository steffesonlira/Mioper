package com.cursoandroid.mioper.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.cursoandroid.mioper.model.Local;
import com.cursoandroid.mioper.R;
import com.cursoandroid.mioper.model.Requisicao;
import com.cursoandroid.mioper.firebase.UsuarioFirebase;
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase;
import com.cursoandroid.mioper.model.Destino;
import com.cursoandroid.mioper.model.UserProfile;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class Corrida extends AppCompatActivity implements OnMapReadyCallback {

    //componente
    private Button buttonAceitarCorrida;
    private FloatingActionButton fabRota;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localMotorista;
    private LatLng localMotorista2;
    private LatLng localPassageiro;
    private static LatLng localPassageiroAtual;
    double _latitude;
    double _longitude;
    private String statusDaViagem;
    private UserProfile motorista;
    private UserProfile passageiro;
    private String idRequisicao;
    private Requisicao requisicao;
    private DatabaseReference firebaseRef;
    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;
    private Marker marcadorDestino;
    private String statusRequisicao;
    private boolean requisicaoAtiva;
    private Destino destino;
    private String tipoRequisicao;
    private static boolean verificaEncerramento = false;
    double distancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corrida);

        inicializarComponentes();

        //Recupera dados do usuário
        if (getIntent().getExtras().containsKey("idRequisicao")
                && getIntent().getExtras().containsKey("motorista")) {
            Bundle extras = getIntent().getExtras();
            motorista = (UserProfile) extras.getSerializable("motorista");
            localMotorista = new LatLng(
                    Double.parseDouble(motorista.getLatitude()),
                    Double.parseDouble(motorista.getLongitude())
            );
            idRequisicao = extras.getString("idRequisicao");
            requisicaoAtiva = extras.getBoolean("requisicaoAtiva");

            //ADICIONA LISTENER PARA REQUISIÇÃO
            verificaStatusRequisicao();
        }


    }

    private void verificaStatusRequisicao() {

        DatabaseReference requisicoes = firebaseRef.child("requisicoes")
                .child(idRequisicao);
        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //RECUPERA REQUISIÇÃO
                    requisicao = dataSnapshot.getValue(Requisicao.class);
                    tipoRequisicao = requisicao.getStatus();
                    if (requisicao != null) {
                        passageiro = requisicao.getPassageiro();
                        localPassageiro = new LatLng(
                                Double.parseDouble(passageiro.getLatitude()),
                                Double.parseDouble(passageiro.getLongitude())
                        );
                        statusRequisicao = requisicao.getStatus();
                        destino = requisicao.getDestino();
                        alteraInterfaceStatusRequisicao(statusRequisicao);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void alteraInterfaceStatusRequisicao(String status) {

        switch (status) {
            case Requisicao.STATUS_AGUARDANDO:
                requisicaoAguardando();
                break;
            case Requisicao.STATUS_A_CAMINHO:
                requisicaoACaminho();
                break;
            case Requisicao.STATUS_VIAGEM:
                requisicaoViagem();
                break;
            case Requisicao.STATUS_FINALIZADA:
                requisicaoFinalizada();
                break;
            case Requisicao.STATUS_CANCELADA:
                requisicaoCancelada();
                break;
        }

    }

    private void requisicaoCancelada() {

    }


    //CHAMADO QUANDO O MOTORISTA CHEGA AO DESTINO
    @SuppressLint("RestrictedApi")
    private void requisicaoFinalizada() {

        locationManager.removeUpdates(locationListener);

        fabRota.setVisibility(View.GONE);
        requisicaoAtiva = false;

        if (marcadorMotorista != null)
            marcadorMotorista.remove();

        if (marcadorDestino != null)
            marcadorDestino.remove();

        //Exibe marcador de destino
        LatLng localDestino = new LatLng(
                Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude())
        );
        adicionaMarcadorDestino(localDestino, "Destino");
        centralizarMarcador(localDestino);

        if (verificaEncerramento == true) {
            //CALCULA DISTANCIA QUANDO VIAGEM É ENCERRADA ANTES
            distancia = Local.calcularDistancia(localPassageiro, localMotorista);
            System.out.println("LOCAL MOTORISTA" + localPassageiro + localMotorista);
        } else {
            //CALCULA DISTANCIA QUANDO VIAGEM É FINALIZADA AUTOMATICAMENTE PELO SISTEMA
            distancia = Local.calcularDistancia(localPassageiro, localDestino);
        }
        double valor = distancia * 8;//4.56
        DecimalFormat decimal = new DecimalFormat("0.00");
        String resultado = decimal.format(valor);

        buttonAceitarCorrida.setEnabled(false);
        buttonAceitarCorrida.setText("Corrida finalizada - R$ " + resultado);

        AlertDialog.Builder builder = new AlertDialog.Builder(Corrida.this, R.style.AlertDialogTheme);
        View view2 = LayoutInflater.from(Corrida.this).inflate(R.layout.layout_successok_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccessOk));
        builder.setView(view2);
        ((TextView) view2.findViewById(R.id.textTitleSuccessOk)).setText(getResources().getString(R.string.success_title_enc_viagem));
        ((TextView) view2.findViewById(R.id.textMessageSuccessOk)).setText("O valor a ser recebido é de R$" + resultado);
        ((Button) view2.findViewById(R.id.buttonSuccessOk)).setText("Encerrar Viagem");
        ((ImageView) view2.findViewById(R.id.imageIconSuccessOk)).setImageResource(R.drawable.logo);

        try {
            final AlertDialog alertDialog = builder.create();

            view2.findViewById(R.id.buttonSuccessOk).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    //VOLTA PARA A TELA DE REQUISIÇÃO
                    requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
                    requisicao.atualizarStatus();
                    verificaEncerramento = false;
                    alertDialog.dismiss();

                    //VOLTA PARA TELA DE REQUISIÇÕES
                    finish();
                }
            });

            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            alertDialog.show();
        } catch (Exception e) {
            System.out.println("ERRO" + e.getMessage());
        }


    }

    private void centralizarMarcador(LatLng local) {
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(local, 18)
        );

    }

    private void requisicaoAguardando() {

        buttonAceitarCorrida.setText("Aceitar corrida");

        //Exibe marcador do motorista
        adicionaMarcadorMotorista(localMotorista, motorista.getName());
        centralizarMarcador(localMotorista);

    }

    @SuppressLint("RestrictedApi")
    private void requisicaoACaminho() {

        //PERMITE FINALIZAR A VIAGEM SOMENTE QUANDO STATUS FOR "viagem"
        statusDaViagem = "acaminho";

        buttonAceitarCorrida.setText("A caminho do passageiro");
        buttonAceitarCorrida.setEnabled(false);
        fabRota.setVisibility(View.VISIBLE);

        //Exibe marcador do motorista
        adicionaMarcadorMotorista(localMotorista, motorista.getName());

        //Exibe marcador passageiro
        adicionaMarcadorPassageiro(localPassageiro, passageiro.getName());

        //Centralizar dois marcadores
        centralizarDoisMarcadores(marcadorMotorista, marcadorPassageiro);

        //Inicia monitoramento do motorista / passageiro
        iniciarMonitoramento(motorista, localPassageiro, Requisicao.STATUS_VIAGEM);

    }


    @SuppressLint("RestrictedApi")
    private void requisicaoViagem() {

        statusDaViagem = "viagem";

        //Altera interface
        fabRota.setVisibility(View.VISIBLE);
        buttonAceitarCorrida.setText("A caminho do destino");

        //Exibe marcador do motorista
        adicionaMarcadorMotorista(localMotorista, motorista.getName());

        //Exibe marcador de destino
        LatLng localDestino = new LatLng(
                Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude())
        );
        adicionaMarcadorDestino(localDestino, "Destino");

        //Centraliza marcadores motorista / destino
        centralizarDoisMarcadores(marcadorMotorista, marcadorDestino);

        //Inicia monitoramento do motorista / passageiro
        iniciarMonitoramento(motorista, localDestino, Requisicao.STATUS_FINALIZADA);

    }

    private void iniciarMonitoramento(final UserProfile uOrigem, LatLng localDestino, final String status) {
        if (!status.equals("finalizada")) {

            //Inicializar GeoFire
            DatabaseReference localUsuario = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("local_usuario");
            GeoFire geoFire = new GeoFire(localUsuario);


            //Adiciona círculo no passageiro
            final Circle circulo = mMap.addCircle(
                    new CircleOptions()
                            .center(localDestino)
                            .radius(30)//em metros
                            .fillColor(Color.argb(90, 255, 153, 0))
                            .strokeColor(Color.argb(190, 255, 152, 0))
            );

            final GeoQuery geoQuery = geoFire.queryAtLocation(
                    new GeoLocation(localDestino.latitude, localDestino.longitude),
                    0.03//em km (0.03 30 metros)
            );
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {

                    if (key.equals(uOrigem.getId())) {
                        Log.d("onKeyEntered", "onKeyEntered: motorista está dentro da área!");


                        //Altera status da requisicao
                        requisicao.setStatus(status);
                        requisicao.atualizarStatus();

                        //Remove listener
                        geoQuery.removeAllListeners();
                        circulo.remove();

                    }

                }

                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });

        }//if
    }

    private void centralizarDoisMarcadores(Marker marcador1, Marker marcador2) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(marcador1.getPosition());
        builder.include(marcador2.getPosition());

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.30);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, largura, altura, espacoInterno)
        );

    }

    private void adicionaMarcadorMotorista(LatLng localizacao, String titulo) {

        if (marcadorMotorista != null)
            marcadorMotorista.remove();

        marcadorMotorista = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.van3))
        );

    }

    private void adicionaMarcadorPassageiro(LatLng localizacao, String titulo) {

        if (marcadorPassageiro != null)
            marcadorPassageiro.remove();

        marcadorPassageiro = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.passageiro))
        );

    }

    private void adicionaMarcadorDestino(LatLng localizacao, String titulo) {

        if (marcadorPassageiro != null)
            marcadorPassageiro.remove();

        if (marcadorDestino != null)
            marcadorDestino.remove();

        marcadorDestino = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.chegada))
        );

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar localizacao do usuário
        recuperarLocalizacaoUsuario();

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    //recuperar latitude e longitude
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    localMotorista = new LatLng(latitude, longitude);

                    //Atualizar GeoFire
                    UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                    //Atualizar localização motorista no Firebase
                    motorista.setLatitude(String.valueOf(latitude));
                    motorista.setLongitude(String.valueOf(longitude));
                    requisicao.setMotorista(motorista);
                    requisicao.atualizarLocalizacaoMotorista();

                    alteraInterfaceStatusRequisicao(statusRequisicao);
                } catch (Exception e) {
                    System.out.println("OCORREU ALGUM ERRO INTERNO" + e.getMessage());
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

        //RECUPERA A LOCALIZAÇÃO (LATITUDE E LONGITUDE) AUTOMATICAMENTE DO USUÁRIO
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }
    }


    //BOTÃO ACEITAR CORRIDA
    public void aceitarCorrida(View view) {
        verificaStatusRequisicao();
        if (!tipoRequisicao.equals("cancelada")) {
            //Configura requisicao
            requisicao = new Requisicao();
            requisicao.setId(idRequisicao);
            requisicao.setMotorista(motorista);
            requisicao.setStatus(Requisicao.STATUS_A_CAMINHO);
            requisicao.atualizar();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(Corrida.this, R.style.AlertDialogTheme);
            View view2 = LayoutInflater.from(Corrida.this).inflate(R.layout.layout_successok_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccessOk));
            builder.setView(view2);
            ((TextView) view2.findViewById(R.id.textTitleSuccessOk)).setText(getResources().getString(R.string.warning_title_ok));
            ((TextView) view2.findViewById(R.id.textMessageSuccessOk)).setText(getResources().getString(R.string.text_desc_ok_viagem_cancelada));
            ((Button) view2.findViewById(R.id.buttonSuccessOk)).setText(getResources().getString(R.string.confirmar));
            ((ImageView) view2.findViewById(R.id.imageIconSuccessOk)).setImageResource(R.drawable.logo);

            final AlertDialog alertDialog = builder.create();
            view2.findViewById(R.id.buttonSuccessOk).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    finish();
                }
            });
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            alertDialog.show();
        }

    }

    private void inicializarComponentes() {

        buttonAceitarCorrida = findViewById(R.id.buttonAceitarCorrida);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Adiciona evento de clique no FabRota
        fabRota = findViewById(R.id.fabRota);
        fabRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = statusRequisicao;
                if (status != null && !status.isEmpty()) {

                    String lat = "";
                    String lon = "";

                    switch (status) {
                        case Requisicao.STATUS_A_CAMINHO:
                            lat = String.valueOf(localPassageiro.latitude);
                            lon = String.valueOf(localPassageiro.longitude);
                            break;
                        case Requisicao.STATUS_VIAGEM:
                            lat = destino.getLatitude();
                            lon = destino.getLongitude();
                            break;
                    }

                    //ABRE ROTA NO GOOGLE MAPS
                    String latLong = lat + "," + lon;
                    Uri uri = Uri.parse("google.navigation:q=" + latLong + "&mode=d");
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    i.setPackage("com.google.android.apps.maps");
                    startActivity(i);

                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (requisicaoAtiva) {
            Toast.makeText(Corrida.this,
                    "Necessário encerrar a requisição atual!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(Corrida.this, Requisicoes.class);
            startActivity(i);
        }

        //Verificar o status da requisição para encerrar
        if (statusRequisicao != null && !statusRequisicao.isEmpty()) {
            requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
            requisicao.atualizarStatus();
        }

        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_nav, menu);
        return true;
    }


    //region onOptionsItemSelected() Ao clicar na seta voltar do toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, Requisicoes.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                break;

            //ENCERRA A VIAGEM ANTECIPADAMENTE PELO MOTORISTA
            case R.id.menuEncerrarViagem:
                if (buttonAceitarCorrida.isEnabled()) {
                    Toast.makeText(this,
                            "A viagem ainda não foi iniciada!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (statusDaViagem.equals("acaminho")) {
                        Toast.makeText(this,
                                "O passageiro ainda não foi embarcado!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        verificaEncerramento = true;
                        statusDaViagem = "finalizada";
                        requisicao.atualizarLocalizacaoAtualPassageiro();
                        requisicao.setStatus(Requisicao.STATUS_FINALIZADA);
                        requisicao.atualizarStatus();
                    }
                }
        }
        return true;
    }
}
