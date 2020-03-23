package com.cursoandroid.mioper;

import butterknife.BindView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cursoandroid.mioper.navigation.MapsActivity;
import com.cursoandroid.mioper.navigation.RequisitionActivity;
import com.google.firebase.database.DatabaseError;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

import java.util.ArrayList;


public class MeusDados extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Spinner spinner;
    ArrayList<CustomItem> customList;
    private FirebaseAuth mAuth;

    //Variáveis Bind para alteração de cadastro do usuário
    @BindView(R.id.profile_name) TextView PName;
    @BindView(R.id.profile_adress) EditText PAdress;
    @BindView(R.id.profile_email) EditText PEmail;
    @BindView(R.id.profile_cpf) EditText PCPF;
    @BindView(R.id.profile_mobile) EditText PMobile;
    @BindView(R.id.profile_nascimento) EditText PNascimento;
    @BindView(R.id.btn_alterar_cadastro) Button btnAltera;
   // @BindView(R.id.encerra_conta) TextView TxtEcerra;
    Button txtEncerra;
    ProgressBar progressBar;
    public TextView name1;
    FirebaseDatabase database;
    DatabaseReference reference;
    private static final String USER = "Users";
    private final String TAG = this.getClass().getName().toUpperCase();
    String email;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    //Resgatar dados firebase


    DrawerLayout drawer;
    //Fim da declaração das variáveis


    //region ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_dados);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = new ProgressBar(this);
        txtEncerra = findViewById(R.id.encerra_conta);
        name1 = findViewById(R.id.name1);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
       //recuperar dados
        //DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference userRef = rootRef.child(USER);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        Query query = reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String name =""+ds.child("name").getValue();
                    String email =""+ds.child("email").getValue();
                    String adress =""+ds.child("adress").getValue();
                    String mobile =""+ds.child("mobile").getValue();

                    //set data
                    name1.setText(email);

                    try{
                        //Picasso.get().load()

                    }catch (Exception e){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        Log.v("Users", userRef.getKey());
//        userRef.addValueEventListener(new ValueEventListener() {
//            String mail;
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot keyId : dataSnapshot.getChildren()){
//                    if(keyId.child("email").getValue().equals(email)){
//                        mail = keyId.child("email").getValue(String.class);
//                        break;
//                    }
//                }
//                name1.setText(mail);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Cadastro cadastro = new Cadastro();


        spinner = findViewById(R.id.SPGenre);
        PMobile = findViewById(R.id.profile_mobile);
        PCPF = findViewById(R.id.profile_cpf);
        PNascimento = findViewById(R.id.profile_nascimento);

        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);

        //Realizando a inserção de dados dentro do Spinner
       // customList = get
        String[] generos = {"  ", "Feminino", "Masculino", "Outros"};



        //Colocando máscaras no Input Text
        PMobile.addTextChangedListener(Mask.mask(PMobile, Mask.FORMAT_FONE));
        PCPF.addTextChangedListener(Mask.mask(PCPF, Mask.FORMAT_CPF));
        PNascimento.addTextChangedListener(Mask.mask(PNascimento, Mask.FORMAT_DATE));

//        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(email_salvo);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String name = dataSnapshot.child("name").getValue().toString();
//                String email = dataSnapshot.child("email").getValue().toString();
//                String adress = dataSnapshot.child("adress").getValue().toString();
//                String mobile = dataSnapshot.child("mobile").getValue().toString();
//
//                PName.setText(name);
//                PEmail.setText(email);
//                PAdress.setText(adress);
//                PMobile.setText(mobile);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



    }

    //endregion

    public void encerraConta(View v){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Atribuindo um relacionamento pai
        DatabaseReference reference = database.getReference(mAuth.getUid());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Conta desativada com sucesso!", Toast.LENGTH_LONG);
                }else{
                    Toast.makeText(getApplicationContext(), "Não foi possível desativar sua conta.", Toast.LENGTH_LONG);

                }
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            final ProgressDialog progressDialog = new ProgressDialog(MeusDados.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Desativando sua Conta...");
            progressDialog.show();
            user.delete().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Conta desativada com sucesso!", Toast.LENGTH_LONG);
                }else{
                    Toast.makeText(getApplicationContext(), "Não foi possível desativar sua conta.", Toast.LENGTH_LONG);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent h= new Intent(MeusDados.this,Principal.class);
        startActivity(h);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent h= new Intent(MeusDados.this, Principal.class);
                startActivity(h);
                break;
            case R.id.nav_data:
                Intent i= new Intent(MeusDados.this,MeusDados.class);
                startActivity(i);
                break;
            case R.id.nav_payment:
                Intent g= new Intent(MeusDados.this,GerenciarPagamentos.class);
                startActivity(g);
                break;
            case R.id.nav_travel_history:
                Intent s= new Intent(MeusDados.this,HistoricoViagens.class);
                startActivity(s);
            case R.id.nav_indication:
                Intent t= new Intent(MeusDados.this,IndiqueGanhe.class);
                startActivity(t);
                break;
            case R.id.nav_game:
                Intent u = new Intent(MeusDados.this, SuporteUsuario.class);
                startActivity(u);
                break;
            case R.id.nav_exit:

                if(item.getItemId() == R.id.nav_exit){

                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                break;


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    public void onRequestPermissionsResult(int RequestCode, String permissions[], int[] grantResults){
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

        }else{

        }
        return;
    }

    private String getFileExtension(Uri uri){

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
