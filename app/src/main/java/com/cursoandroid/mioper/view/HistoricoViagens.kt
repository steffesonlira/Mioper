package com.cursoandroid.mioper.view;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cursoandroid.mioper.R;
import com.cursoandroid.mioper.adapter.AdapterHistoricoViagem;
import com.cursoandroid.mioper.firebase.ConfiguracaoFirebase;
import com.cursoandroid.mioper.model.HistoricoUsuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.cursoandroid.mioper.firebase.UsuarioFirebase.getIdentificadorUsuario;
import static com.cursoandroid.mioper.firebase.UsuarioFirebase.getUsuarioAtual;

public class HistoricoViagens extends AppCompatActivity {

    String nomeUsuario1;
    String celularUsuario;
    String senhaUsuario;
    String repitasenha;
    String emailUsuario;
    String enderecoUsuario;
    String nascimentoUsuario;
    String cpfUsuario;
    String generoUsuario;
    TextView historicoUser;
    Principal activity;
    String tipoUsuario;
    private ListView listLocais;
    private RecyclerView recyclerView;
    public static ArrayAdapter adapter;
    public static ArrayList<String> array = new ArrayList<>();
    public ArrayList<HistoricoUsuario> array2 = new ArrayList<>();
    String userMailReplaced;
    Button btnListar;
    DataSnapshot dSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_historico_viagens);


        recyclerView = findViewById(R.id.recyclerView);
        historicoUser = findViewById(R.id.textView_viagem);

        //DEIXA A LABEL "VOCÊ NÃO POSSUI HISTORICO DE VIAGENS" INVISÍVEL NA TELA
        historicoUser.setVisibility(View.INVISIBLE);

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Histórico de Viagens");
        //endregion

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

                    //VERIFICA SE RETORNOU DADOS DA CONSULTA  NO FIREBASE
                    if (dataSnapshot.exists()) {
                        mostrarHistorico(dataSnapshot);
                    } else {
                        historicoUser.setVisibility(View.VISIBLE); //DEIXA A LABEL "VOCÊ NÃO POSSUI HISTORICO DE VIAGENS" VISÍVEL NA TELA
                    }
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


    //region Criação do Menu Toolbar XML
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal
                , menu);
        return true;
    }
    //endregion

    //region onOptionsItemSelected() Ao clicar na seta voltar do toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                finish();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }


}
