package com.cursoandroid.mioper.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cursoandroid.mioper.R;
import com.cursoandroid.mioper.view.Login;
import com.google.firebase.auth.FirebaseAuth;

public class RequisitionActivity extends AppCompatActivity {


    //VARIABLES
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisition);
    }


    public void logOut(View view) {
        firebaseAuth.getInstance().signOut();
        finish();

        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);

    }
}
