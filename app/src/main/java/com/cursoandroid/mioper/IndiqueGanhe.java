package com.cursoandroid.mioper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.view.MenuItem;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class IndiqueGanhe extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Mioper" ;
    private AdapterViewFlipper adapterViewFlipper;
    private static final String[] TEXT = {"Entre no App do Mioper","Procure pelo Mioper mais próximo","O Motorista aceitará sua solicitação","Partiu Viajar com o Mioper"};
    //private static final int[] IMAGES = {R.drawable.vanindique4, R.drawable.vanindique1,R.drawable.vanindique2,R.drawable.mioperviagem};
    private FirebaseAuth mAuth;
    private Button btnInvite;
    private FirebaseAnalytics analytics;
    public TextView txtCodigo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_indique_ganhe);
        btnInvite = findViewById(R.id.btnInvite);
        mAuth = FirebaseAuth.getInstance();
        adapterViewFlipper = findViewById(R.id.idAdapterVF);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        txtCodigo = findViewById(R.id.txtCodigo);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//       drawer.addDrawerListener(toggle);
//        toggle.syncState();
  //      navigationView.setNavigationItemSelectedListener(this);

        //Criando objeto adapter
//        FlipperAdapter adapter = new FlipperAdapter(this, TEXT,IMAGES);
//        adapterViewFlipper.setAdapter(adapter);
//        adapterViewFlipper.setAutoStart(true);

        //click botão Invite
        btnInvite.setOnClickListener(v -> {
            onInviteClicked();
        });
    }

    class FlipperAdapter extends BaseAdapter{
        Context ctx;
        int[] images;
        String[] text;
        LayoutInflater inflater;

        public FlipperAdapter(Context context, String[] myText, int[] myImages){
            this.ctx = context;
            this.images = myImages;
            this.text = myText;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount(){
            return text.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
           view = inflater.inflate(R.layout.sliderimages, null);
           TextView txtName = view.findViewById(R.id.idTxtImageView);
           ImageView txtImage = view.findViewById(R.id.idImgView);
           txtName.setText(text[i]);
           txtImage.setImageResource(images[i]);
           return view;
        }
    }

    private void onInviteClicked(){
        //get text from blovetext1
        String text = txtCodigo.getText().toString();
        //Sharing intent
        Intent mSharingIntent = new Intent(Intent.ACTION_SEND);
        mSharingIntent.setType("text/plain");
        mSharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Write your subject here");
        mSharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(mSharingIntent,"Share text via"));
    }

    @Override
    public void onBackPressed() {
        Intent h= new Intent(IndiqueGanhe.this,Principal.class);
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
                Intent h= new Intent(IndiqueGanhe.this,Principal.class);
                startActivity(h);
                break;
            case R.id.nav_data:
                Intent i= new Intent(IndiqueGanhe.this,MeusDados.class);
                startActivity(i);
                break;
            case R.id.nav_payment:
                Intent g= new Intent(IndiqueGanhe.this,GerenciarPagamentos.class);
                startActivity(g);
                break;
            case R.id.nav_travel_history:
                Intent s= new Intent(IndiqueGanhe.this,HistoricoViagens.class);
                startActivity(s);
            case R.id.nav_indication:
                Intent t= new Intent(IndiqueGanhe.this,IndiqueGanhe.class);
                startActivity(t);
                break;
            case R.id.nav_game:
                Intent u = new Intent(IndiqueGanhe.this, SuporteUsuario.class);
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



}
