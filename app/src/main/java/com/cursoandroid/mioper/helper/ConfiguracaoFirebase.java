package com.cursoandroid.mioper.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jamiltondamasceno
 */

public class ConfiguracaoFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth auth;

    //retorna a instancia do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase(){

        if ( database == null ){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao(){

        if( auth == null ){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

}
