package com.cursoandroid.mioper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cursoandroid.mioper.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.cursoandroid.mioper.R;

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
