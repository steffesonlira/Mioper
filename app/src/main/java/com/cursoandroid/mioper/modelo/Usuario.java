package com.cursoandroid.mioper.modelo;

import com.cursoandroid.mioper.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Usuario {
    private String name;
    private String address;
    private String email_semreplace;
    private String email;
    private String mobile;
    private String password;
    private String reEnterPassword;

    public Usuario(){

    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child( "Users" ).child( getEmail() );

        usuarios.setValue(this);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail_semreplace() {
        return email_semreplace;
    }

    public void setEmail_semreplace(String email_semreplace) {
        this.email_semreplace = email_semreplace;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReEnterPassword() {
        return reEnterPassword;
    }

    public void setReEnterPassword(String reEnteraPassword) {
        this.reEnterPassword = reEnteraPassword;
    }
}
