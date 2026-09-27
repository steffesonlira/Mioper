package com.cursoandroid.mioper.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.cursoandroid.mioper.R;
import com.cursoandroid.mioper.model.Requisicao;
import com.cursoandroid.mioper.firebase.UsuarioFirebase;
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase;
import com.cursoandroid.mioper.model.Destino;
import com.cursoandroid.mioper.model.Local;
import com.cursoandroid.mioper.model.UserProfile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Passageiro extends AppCompatActivity implements OnMapReadyCallback {

    //Componentes
    private EditText editDestino;
    private LinearLayout linearLayoutDestino;
    private Button buttonChamarUber;
    private double distancia;
    private GoogleMap mMap;
    private FirebaseAuth autenticacao;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localPassageiro;
    private boolean cancelarUber = false;
    private DatabaseReference firebaseRef;
    private Requisicao requisicao;
    private String tipoRequisicao;
    private UserProfile passageiro;
    private String statusRequisicao;
    private Destino destino;
    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;
    private Marker marcadorDestino;
    private UserProfile motorista;
    private LatLng localMotorista;
    private LatLng localMotoristaAtual;
    public static String endereco;
    public static String cidade;
    public static String bairro;
    private double _latitude;
    private double _longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiro);

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Navegue com o Mioper");
        //endregion

        inicializarComponentes();

        //Adiciona listener para requisição
        verificaStatusRequisicao();

    }

    private void verificaStatusRequisicao() {

        UserProfile usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("passageiro/id")
                .equalTo(usuarioLogado.getId());

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Requisicao> lista = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lista.add(ds.getValue(Requisicao.class));
                }

                Collections.reverse(lista);
                if (lista != null && lista.size() > 0) {
                    requisicao = lista.get(0);
                    tipoRequisicao = requisicao.getStatus();

                    if (requisicao != null) {


                        if (!requisicao.getStatus().equals(Requisicao.STATUS_ENCERRADA)) {
                            passageiro = requisicao.getPassageiro();
                            localPassageiro = new LatLng(
                                    Double.parseDouble(passageiro.getLatitude()),
                                    Double.parseDouble(passageiro.getLongitude())
                            );
                            statusRequisicao = requisicao.getStatus();
                            destino = requisicao.getDestino();


                            //RECUPERA LOCALIZAÇÃO ATUAL DO PASSAGEIRO (QUANDO A VIAGEM É ENCERRADA ANTECIPADAMENTE PELO MOTORISTA)
                            if (passageiro.getLatitude_atual() != null && passageiro.getLongitude_atual() != null) {
                                localMotoristaAtual = new LatLng(
                                        Double.parseDouble(passageiro.getLatitude_atual()),
                                        Double.parseDouble(passageiro.getLongitude_atual())
                                );
                            }


                            if (requisicao.getMotorista() != null) {
                                motorista = requisicao.getMotorista();
                                localMotorista = new LatLng(
                                        Double.parseDouble(motorista.getLatitude()),
                                        Double.parseDouble(motorista.getLongitude())
                                );
                            }

                            alteraInterfaceStatusRequisicao(statusRequisicao);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void alteraInterfaceStatusRequisicao(String status) {

        if (status != null && !status.isEmpty()) {
            cancelarUber = false;
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
        } else {
            //Adiciona marcador passageiro
            adicionaMarcadorPassageiro(localPassageiro, "Seu local");
            centralizarMarcador(localPassageiro);
        }

    }

    private void requisicaoCancelada() {

        linearLayoutDestino.setVisibility(View.VISIBLE);
        buttonChamarUber.setText("Chamar Mioper");
        cancelarUber = false;

    }

    private void requisicaoAguardando() {

        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setText("Cancelar Mioper");
        cancelarUber = true;

        //Adiciona marcador passageiro
        adicionaMarcadorPassageiro(localPassageiro, passageiro.getName());
        centralizarMarcador(localPassageiro);

    }

    private void requisicaoACaminho() {

        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setText("Motorista a caminho");
        buttonChamarUber.setEnabled(false);

        //Adiciona marcador passageiro
        adicionaMarcadorPassageiro(localPassageiro, passageiro.getName());

        //Adiciona marcador motorista
        adicionaMarcadorMotorista(localMotorista, motorista.getName());

        //Centralizar passageiro / motorista
        centralizarDoisMarcadores(marcadorMotorista, marcadorPassageiro);

    }

    private void requisicaoViagem() {

        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setText("A caminho do destino");
        buttonChamarUber.setEnabled(false);

        //Adiciona marcador motorista
        adicionaMarcadorMotorista(localMotorista, motorista.getName());

        //Adiciona marcador de destino
        LatLng localDestino = new LatLng(
                Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude())
        );
        adicionaMarcadorDestino(localDestino, "Destino");

        //Centraliza marcadores motorista / destino
        centralizarDoisMarcadores(marcadorMotorista, marcadorDestino);

    }

    //CHAMADO QUANDO O PASSAGEIRO CHEGA AO DESTINO
    private void requisicaoFinalizada() {


        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setEnabled(false);

        //Adiciona marcador de destino
        LatLng localDestino = new LatLng(
                Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude())
        );
        adicionaMarcadorDestino(localDestino, "Destino");
        centralizarMarcador(localDestino);


        if (localMotoristaAtual != null) {
            distancia = Local.calcularDistancia(localPassageiro, localMotoristaAtual);
            System.out.println("LOCAL PASSAGEIRO" + localPassageiro + localMotoristaAtual);
        } else {
            distancia = Local.calcularDistancia(localPassageiro, localDestino);
        }

        double valor = distancia * 8;
        DecimalFormat decimal = new DecimalFormat("0.00");
        String resultado = decimal.format(valor);

        buttonChamarUber.setText("Corrida finalizada - R$ " + resultado);

        AlertDialog.Builder builder = new AlertDialog.Builder(Passageiro.this, R.style.AlertDialogTheme);
        View view2 = LayoutInflater.from(Passageiro.this).inflate(R.layout.layout_successok_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccessOk));
        builder.setView(view2);
        ((TextView) view2.findViewById(R.id.textTitleSuccessOk)).setText(getResources().getString(R.string.success_title_enc_viagem));
        ((TextView) view2.findViewById(R.id.textMessageSuccessOk)).setText("Sua viagem ficou: R$ " + resultado);
        ((Button) view2.findViewById(R.id.buttonSuccessOk)).setText("Encerrar Viagem");
        ((ImageView) view2.findViewById(R.id.imageIconSuccessOk)).setImageResource(R.drawable.logo);

        try {
            final AlertDialog alertDialog = builder.create();

            view2.findViewById(R.id.buttonSuccessOk).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
                    requisicao.atualizarStatus();
                    localMotoristaAtual = null;
                    alertDialog.dismiss();

                    //VOLTA PARA A TELA PRINCIPAL
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

    private void centralizarMarcador(LatLng local) {
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(local, 18)
        );
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar localizacao do usuário
        recuperarLocalizacaoUsuario();

    }

    //BOTÃO CHAMAR MIOPER
    public void chamarUber(View view) {

        //VERIFICAÇÃO SE GPS DO CELULAR ESTÁ LIGADO
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsStatus == false) {
            Toast.makeText(this,
                    "A localização do seu celular está desligada ou o sinal está muito fraco.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(isOnline(this) == false){
            Toast.makeText(this,
                    "Sem conexão com a internet",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        //false -> uber não pode ser cancelado ainda
        //true -> uber pode ser cancelado
        if (cancelarUber) {//Uber pode ser cancelado

            //Cancelar a requisição
            if (tipoRequisicao.equals("aguardando")) {
                requisicao.setStatus(Requisicao.STATUS_CANCELADA);
                requisicao.atualizarStatus();
                requisicao.excluirHistorico();
            }
        } else {

            //RECUPERA ENDEREÇO ATUAL DO PASSAGEIRO ATRAVES DA LATITUDE E LONGITUDE
            getCompleteAddressString(_latitude, _longitude);


            String enderecoDestino = editDestino.getText().toString();

            //SE USUÁRIO INFORMAR O ENDEREÇO
            if (!enderecoDestino.equals("")) {

                //DATA E HORA ATUAL
                SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                Date data = new Date();
                String dataFormatada = formataData.format(data);
                //========

                Address addressDestino = recuperarEndereco(enderecoDestino);
                if (addressDestino != null) {
                    final Destino destino = new Destino();
                    destino.setData(dataFormatada);


                    if (addressDestino.getSubAdminArea() == null) {
                        destino.setCidade("N/A");
                    } else {
                        destino.setCidade(addressDestino.getSubAdminArea());
                    }

                    if (addressDestino.getPostalCode() == null) {
                        destino.setCep("N/A");
                    } else {
                        destino.setCep(addressDestino.getPostalCode());
                    }

                    if (addressDestino.getSubLocality() == null) {
                        destino.setBairro("N/A");
                    } else {
                        destino.setBairro(addressDestino.getSubLocality());
                    }

                    if (addressDestino.getThoroughfare() == null) {
                        destino.setRua("N/A");
                    } else {
                        destino.setRua(addressDestino.getThoroughfare());
                    }

                    if (addressDestino.getFeatureName() == null) {
                        destino.setNumero("N/A");
                    } else {
                        destino.setNumero(addressDestino.getFeatureName());
                    }

                    destino.setLatitude(String.valueOf(addressDestino.getLatitude()));
                    destino.setLongitude(String.valueOf(addressDestino.getLongitude()));


                    //CONTROI O ARRAY DE CONFIRMAÇÃO DE ENDEREÇO AO CHAMAR O MIOPER
                    StringBuilder mensagem = new StringBuilder();

                    if (destino.getCidade() != null) {
                        mensagem.append("Cidade: " + destino.getCidade());
                    } else {

                        mensagem.append("Cidade: " + "N/A");
                    }


                    if (destino.getRua() != null) {

                        mensagem.append("\nRua: " + destino.getRua());
                    } else {
                        mensagem.append("\nRua: " + "N/A");
                    }


                    if (destino.getBairro() != null) {

                        mensagem.append("\nBairro: " + destino.getBairro());
                    } else {
                        mensagem.append("\nBairro: " + "N/A");
                    }


                    AlertDialog.Builder builder = new AlertDialog.Builder(Passageiro.this, R.style.AlertDialogTheme);
                    View view2 = LayoutInflater.from(Passageiro.this).inflate(R.layout.layout_success_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSuccessOk));
                    builder.setView(view2);
                    ((TextView) view2.findViewById(R.id.textTitleSuccess)).setText(getResources().getString(R.string.success_title_confirme_viagem));
                    ((TextView) view2.findViewById(R.id.textMessageSuccess)).setText(mensagem);
                    ((Button) view2.findViewById(R.id.buttonConfirmaSuccess)).setText(getResources().getString(R.string.confirmar));
                    ((Button) view2.findViewById(R.id.buttonCancelSuccess)).setText(getResources().getString(R.string.cancelar));
                    ((ImageView) view2.findViewById(R.id.imageIconSuccess)).setImageResource(R.drawable.logo);

                    final AlertDialog alertDialog = builder.create();

                    view2.findViewById(R.id.buttonConfirmaSuccess).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            //salvar requisição
                            salvarRequisicao(destino);

                            //SALVAR HISTORICO DO USUÁRIO
                            Requisicao requisicao1 = new Requisicao();
                            requisicao1.salvarHistorico(destino);

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

            } else {
                Toast.makeText(this,
                        "Informe o endereço de destino!",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }


    //RECUPERA ENDEREÇO ATUAL DO PASSAGEIRO ATRAVES DA LATITUDE E LONGITUDE
    private void getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null && addresses.size() > 0) {
                endereco = addresses.get(0).getThoroughfare(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                cidade = addresses.get(0).getSubAdminArea();
                bairro = addresses.get(0).getSubLocality();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void salvarRequisicao(Destino destino) {

        Requisicao requisicao = new Requisicao();
        requisicao.setDestino(destino);

        UserProfile usuarioPassageiro = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioPassageiro.setLatitude(String.valueOf(localPassageiro.latitude));
        usuarioPassageiro.setLongitude(String.valueOf(localPassageiro.longitude));
        usuarioPassageiro.enderecoAtualUsuario = endereco;
        usuarioPassageiro.cidadeAtualUsuario = cidade;
        usuarioPassageiro.bairroAtualUsuario = bairro;


        requisicao.setPassageiro(usuarioPassageiro);
        requisicao.setStatus(Requisicao.STATUS_AGUARDANDO);
        requisicao.salvar();

        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setText("Cancelar Mioper");

    }

    //RECUPERA ENDEREÇO DIGITADO PELO USUÁRIO E VERIFICA NA NUVEM SE EXISTE
    private Address recuperarEndereco(String endereco) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco, 1);
            if (listaEnderecos != null && listaEnderecos.size() > 0) {
                Address address = listaEnderecos.get(0);

                return address;

            } else {
                Toast.makeText(this,
                        "Ops! Endereço não encontrado.",
                        Toast.LENGTH_SHORT).show();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    //RECUPERA LATITUDE E LONGITUDE ATUAL DO USUARIO
    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                _latitude = latitude;
                _longitude = longitude;


                localPassageiro = new LatLng(latitude, longitude);

                //Atualizar GeoFire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                //Altera interface de acordo com o status
                alteraInterfaceStatusRequisicao(statusRequisicao);

                if (statusRequisicao != null && !statusRequisicao.isEmpty()) {
                    if (statusRequisicao.equals(Requisicao.STATUS_VIAGEM)
                            || statusRequisicao.equals(Requisicao.STATUS_FINALIZADA)) {
                        locationManager.removeUpdates(locationListener);
                    } else {

                        //RECUPERA A LOCALIZAÇÃO (LATITUDE E LONGITUDE) AUTOMATICAMENTE DO USUÁRIO
                        if (ActivityCompat.checkSelfPermission(Passageiro.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    10000,
                                    10,
                                    locationListener
                            );
                        }
                    }
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
                    10000,
                    10,
                    locationListener
            );
        }


    }


    private void inicializarComponentes() {

        //Inicializar componentes
        editDestino = findViewById(R.id.editDestino);
        linearLayoutDestino = findViewById(R.id.linearLayoutDestino);
        buttonChamarUber = findViewById(R.id.buttonChamarUber);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
            case android.R.id.home:
                //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                finish();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }


    //VERIFICA SE EXISTE CONEXÃO COM A INTERNET
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
            return true;
        else
            return false;
    }


}
