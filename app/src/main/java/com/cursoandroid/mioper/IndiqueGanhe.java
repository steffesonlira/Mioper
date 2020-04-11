package com.cursoandroid.mioper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class IndiqueGanhe extends AppCompatActivity {
    private static final String TAG = "Mioper";
    private AdapterViewFlipper adapterViewFlipper;
    private static final String[] TEXT = {"Entre no App do Mioper", "Procure pelo Mioper mais próximo", "O Motorista aceitará sua solicitação", "Partiu Viajar com o Mioper"};
    private static final int[] IMAGES = {R.drawable.indique1, R.drawable.vanindique1,R.drawable.vanindique2,R.drawable.mioperviagem};
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        txtCodigo = findViewById(R.id.txtCodigo);

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Indique e Ganhe");
        //endregion

        //Criando objeto adapter
        FlipperAdapter adapter = new FlipperAdapter(this, TEXT,IMAGES);
        adapterViewFlipper.setAdapter(adapter);
        adapterViewFlipper.setAutoStart(true);

        //click botão Invite
        btnInvite.setOnClickListener(v -> {
            onInviteClicked();
        });
    }

    class FlipperAdapter extends BaseAdapter {
        Context ctx;
        int[] images;
        String[] text;
        LayoutInflater inflater;

        public FlipperAdapter(Context context, String[] myText, int[] myImages) {
            this.ctx = context;
            this.images = myImages;
            this.text = myText;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
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
            txtName.setTextSize(18);
            txtImage.setImageResource(images[i]);
            return view;
        }
    }

    private void onInviteClicked() {
        //get text from blovetext1
        String text = txtCodigo.getText().toString();
        //Sharing intent
        Intent mSharingIntent = new Intent(Intent.ACTION_SEND);
        mSharingIntent.setType("text/plain");
        mSharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Write your subject here");
        mSharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(mSharingIntent, "Share text via"));
    }

    @Override
    public void onBackPressed() {
        Intent h = new Intent(IndiqueGanhe.this, Principal.class);
        startActivity(h);
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
                startActivity(new Intent(this, Principal.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }

}
