package com.cursoandroid.mioper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.cursoandroid.mioper.UsuarioFirebase.getIdentificadorUsuario;
import static com.cursoandroid.mioper.UsuarioFirebase.getUsuarioAtual;

public class HistoricoViagens extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String nomeUsuario1;
    String celularUsuario;
    String senhaUsuario;
    String repitasenha;
    String emailUsuario;
    String enderecoUsuario;
    String nascimentoUsuario;
    String cpfUsuario;
    String generoUsuario;
    Principal activity;
    String tipoUsuario;
    private ListView listLocais;
    private RecyclerView recyclerView;
    public static ArrayAdapter adapter;
    public static ArrayList<String> array = new ArrayList<>();
    public ArrayList<HistoricoUsuario> array2 = new ArrayList<>();
    String userMailReplaced;
    Button btnListar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_historico_viagens);


        recyclerView = findViewById(R.id.recyclerView);
        //PESQUISA HISTORICO NO FIREBASE
        retornaHistorico();
    }

    //ACESSA O HISTORICO DO USUARIO
    public void retornaHistorico() {
        FirebaseUser user = getUsuarioAtual();
        String userEmail = user.getEmail();

        userMailReplaced = userEmail.replace('.', '-');

        if (user != null) {
            Log.d("resultado", "onDataChange: " + getIdentificadorUsuario());
            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("historicoUsuario")
                    .child(userMailReplaced);
            usuariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("resultado", "onDataChange: " + dataSnapshot.toString());


                    mostrarHistorico(dataSnapshot);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    //COLOCA AS INFORMAÇÕES RETORNADAS DO FIREBASE DENTRO DE UM ARRAY
    public void mostrarHistorico(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            HistoricoUsuario uHistorico = new HistoricoUsuario();

            uHistorico.setBairro(ds.getValue(HistoricoUsuario.class).getBairro());
            uHistorico.setCep(ds.getValue(HistoricoUsuario.class).getCep());
            uHistorico.setCidade(ds.getValue(HistoricoUsuario.class).getCidade());
            uHistorico.setData(ds.getValue(HistoricoUsuario.class).getData());
            uHistorico.setRua(ds.getValue(HistoricoUsuario.class).getRua());
            uHistorico.setLatitude(ds.getValue(HistoricoUsuario.class).getLatitude());
            uHistorico.setLongitude(ds.getValue(HistoricoUsuario.class).getLongitude());
            uHistorico.setNumero(ds.getValue(HistoricoUsuario.class).getNumero());


            //ADICIONA O OBJETO HISTORICO COM TODOS OS DADOS NO ARRAY
            array2.add(uHistorico);

            //CHAMA O METODO PARA PASSAR OS DADOS PARA O RECYVLERVIEW
            enviarDadosLista(array2);


        }
    }

    //ENVIA DADOS PARA O RECYCLERVIEW
    public void enviarDadosLista(ArrayList array2) {
        //CONFIGURA ADAPTER
        AdapterHistoricoViagem adapter = new AdapterHistoricoViagem(array2);

        //CONFIGURA RECYCLERVIEW
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent h= new Intent(HistoricoViagens.this,Passageiro.class);
                startActivity(h);
                break;
            case R.id.nav_data:
                Intent i= new Intent(HistoricoViagens.this,MeusDados.class);
                //CHAMA A TELA MEUS DADOS E PASSA OS DADOS
                i.putExtra("name", nomeUsuario1);
                i.putExtra("mobile", celularUsuario);
                i.putExtra("senha", senhaUsuario);
                i.putExtra("email", emailUsuario);
                i.putExtra("adress", enderecoUsuario);
                i.putExtra("nascimento", nascimentoUsuario);
                i.putExtra("cpf", cpfUsuario);
                i.putExtra("genero", generoUsuario);
                i.putExtra("tipouser", tipoUsuario);
                startActivity(i);
                break;
            case R.id.nav_payment:
                Intent g= new Intent(HistoricoViagens.this,GerenciarPagamentos.class);
                startActivity(g);
                break;
            case R.id.nav_historico:
                Intent s= new Intent(HistoricoViagens.this,HistoricoViagens.class);
                startActivity(s);
            case R.id.nav_indication:
                Intent t= new Intent(HistoricoViagens.this,IndiqueGanhe.class);
                startActivity(t);
                break;
            case R.id.nav_game:
                Intent u = new Intent(HistoricoViagens.this, SuporteUsuario.class);
                startActivity(u);
                break;
            case R.id.nav_about_us:
                Intent v = new Intent(HistoricoViagens.this, Sobre.class);
                startActivity(v);
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
