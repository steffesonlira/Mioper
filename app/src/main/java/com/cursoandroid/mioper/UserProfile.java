package com.cursoandroid.mioper;

public class UserProfile {
    public String name;
    public String adress;
    public String email;
    public String cpf;
    public String mobile;
    public String nascimento;

    public UserProfile(){}

public UserProfile(String name, String adress, String email, String cpf, String mobile, String nascimento){
        this.name = name;
        this.email = email;
        this.adress = adress;
        this.cpf = cpf;
        this.mobile = mobile;
        this.nascimento = nascimento;
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
}