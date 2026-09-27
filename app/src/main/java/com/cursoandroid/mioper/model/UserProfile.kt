package com.cursoandroid.mioper.model;

import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String id;
    public String name;
    public String adress;
    public String email;
    public String cpf;
    public String mobile;
    public String nascimento;
    public String senha;
    public String repitasenha;
    private String tipouser;
    private String latitude;
    private String longitude;
    private String latitude_atual;
    private String longitude_atual;
    private String genero;
    public  String enderecoAtualUsuario;
    public  String cidadeAtualUsuario;
    public String bairroAtualUsuario;


    public UserProfile() {
    }

    public UserProfile(String id, String name, String adress, String email, String cpf, String mobile, String nascimento, String senha, String repitasenha, String tipouser, String latitude, String longitude, String genero) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.adress = adress;
        this.cpf = cpf;
        this.mobile = mobile;
        this.nascimento = nascimento;
        this.senha = senha;
        this.repitasenha = repitasenha;
        this.tipouser = tipouser;
        this.setLatitude(latitude);
        this.longitude = longitude;
        this.genero = genero;
    }

    public void salvar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("Users").child(getId());
        usuarios.setValue(this);

    }

    //REMOVER DO DATABASE AS INFORMAÇÕES DO USUÁRIO
    public void remover() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("Users").child(getId());
        usuarios.removeValue();

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getRepitasenha() {
        return repitasenha;
    }

    public void setRepitasenha(String repitasenha) {
        this.repitasenha = repitasenha;
    }

    public String getTipouser() {
        return tipouser;
    }

    public void setTipouser(String tipouser) {
        this.tipouser = tipouser;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }


    public String getLatitude_atual() {
        return latitude_atual;
    }

    public void setLatitude_atual(String latitude_atual) {
        this.latitude_atual = latitude_atual;
    }

    public String getLongitude_atual() {
        return longitude_atual;
    }

    public void setLongitude_atual(String longitude_atual) {
        this.longitude_atual = longitude_atual;
    }

}