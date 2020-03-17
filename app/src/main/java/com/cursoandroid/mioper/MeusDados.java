package com.cursoandroid.mioper;

import butterknife.BindView;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class MeusDados extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Spinner spinner;
    ArrayList<CustomItem> customList;
    private FirebaseAuth mAuth;

    //Variáveis Bind para alteração de cadastro do usuário
    @BindView(R.id.profile_name) EditText PName;
    @BindView(R.id.profile_adress) EditText PAdress;
    @BindView(R.id.profile_email) EditText PEmail;
    @BindView(R.id.profile_cpf) EditText PCPF;
    @BindView(R.id.profile_mobile) EditText PMobile;
    @BindView(R.id.profile_nascimento) EditText PNascimento;
    @BindView(R.id.btn_alterar_cadastro) Button btnAltera;
    @BindView(R.id.encerra_conta) TextView TxtEcerra;
    DrawerLayout drawer;
    //Fim da declaração das variáveis


    //region ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_dados);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


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

    }
    //endregion


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
                Intent h= new Intent(MeusDados.this,Principal.class);
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
                Intent u = new Intent(MeusDados.this,Jogo.class);
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
