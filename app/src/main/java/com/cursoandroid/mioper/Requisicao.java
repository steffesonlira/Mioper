package com.cursoandroid.mioper;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import static com.cursoandroid.mioper.UsuarioFirebase.getUsuarioAtual;

public class Requisicao {
    private String id;
    private String status;
    private UserProfile passageiro;
    private UserProfile motorista;
    private Destino destino;

    public static final String STATUS_AGUARDANDO = "aguardando";
    public static final String STATUS_A_CAMINHO = "acaminho";
    public static final String STATUS_VIAGEM = "viagem";
    public static final String STATUS_FINALIZADA = "finalizada";
    public static final String STATUS_ENCERRADA = "encerrada";
    public static final String STATUS_CANCELADA = "cancelada";
    static String idRequisicaoHistorico;

    public Requisicao() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        String idRequisicao = requisicoes.push().getKey();
        setId( idRequisicao );

        requisicoes.child( getId() ).setValue(this);

    }

    //SALVAR HISTORICO DO USUARIO
    public void salvarHistorico(Destino destino) {

        FirebaseUser firebaseUser = getUsuarioAtual();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        String userEmail = firebaseUser.getEmail();
        String userMailReplaced = userEmail.replace('.', '-');


        DatabaseReference historico = firebaseRef.child("historicoUsuario").child(userMailReplaced);

        idRequisicaoHistorico = historico.push().getKey();
        setId(idRequisicaoHistorico);
        historico.child(getId()).setValue(destino);

    }
    public void excluirHistorico() {

        FirebaseUser firebaseUser = getUsuarioAtual();
        String userEmail = firebaseUser.getEmail();

        String userMailReplaced = userEmail.replace('.', '-');
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("historicoUsuario").child(userMailReplaced).child(idRequisicaoHistorico);

        requisicoes.removeValue();
    }

    public void atualizar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        DatabaseReference requisicao = requisicoes.child(getId());

        Map objeto = new HashMap();
        objeto.put("motorista", getMotorista() );
        objeto.put("status", getStatus());

        requisicao.updateChildren( objeto );

    }

    public void atualizarStatus(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        DatabaseReference requisicao = requisicoes.child(getId());

        Map objeto = new HashMap();
        objeto.put("status", getStatus());

        requisicao.updateChildren( objeto );

    }

    public void atualizarLocalizacaoMotorista(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef
                .child("requisicoes");

        DatabaseReference requisicao = requisicoes
                .child(getId())
                .child("motorista");

        Map objeto = new HashMap();
        objeto.put("latitude", getMotorista().getLatitude() );
        objeto.put("longitude", getMotorista().getLongitude());

        requisicao.updateChildren( objeto );

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserProfile getPassageiro() {
        return passageiro;
    }

    public void setPassageiro(UserProfile passageiro) {
        this.passageiro = passageiro;
    }

    public UserProfile getMotorista() {
        return motorista;
    }

    public void setMotorista(UserProfile motorista) {
        this.motorista = motorista;
    }

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }
}
