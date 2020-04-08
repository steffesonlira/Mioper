package com.cursoandroid.mioper;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MeusDados extends AppCompatActivity {

    EditText nomeUsuario;
    EditText celularUsuario;
    String senhaUsuario;
    EditText emailUsuario;
    EditText enderecoUsuario;
    EditText dataNascimentoUsuario;
    EditText cpfUsuario;
    Switch generoUsuario;
    String tipoUsuario;
    String idUsuario;
    TextView txtMasculino, txtFeminino;
    String email;
    private FirebaseAuth autenticacao;


    //region ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_meus_dados);
        Toolbar toolbar = findViewById(R.id.toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //REFERENCIA OS TEXTVIEW
        nomeUsuario = findViewById(R.id.txtNome);
        celularUsuario = findViewById(R.id.txtCelular);
        txtFeminino = findViewById(R.id.txtFeminino);
        txtMasculino = findViewById(R.id.txtFeminino);
        emailUsuario = findViewById(R.id.txtEmail);
        enderecoUsuario = findViewById(R.id.txtEndereco);
        dataNascimentoUsuario = findViewById(R.id.txtDataNascimento);
        cpfUsuario = findViewById(R.id.txtCpf);
        generoUsuario = findViewById(R.id.switchGenero);



        //AO CLICAR, ALTERA A COR DO SWITCH PASSGEIRO/MOTORISTA
        generoUsuario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (generoUsuario.isChecked()) {
                    txtFeminino.setTextColor(Color.parseColor("#009688"));
                    txtMasculino.setTextColor(Color.parseColor("#FFFFFFFF"));

                } else {
                    txtMasculino.setTextColor(Color.parseColor("#009688"));
                    txtFeminino.setTextColor(Color.parseColor("#FFFFFFFF"));

                }
            }
        });

        //region Criando botão de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Meus Dados");
        //endregion

        //RECEBE DADOS DA TELA PRINCIPAL
        Bundle dados = getIntent().getExtras();
        String nome = dados.getString("name");
        String celular = dados.getString("mobile");
        email = dados.getString("email");
        String endereco = dados.getString("adress");
        senhaUsuario = dados.getString("senha");
        String nascimento = dados.getString("nascimento");
        String cpf = dados.getString("cpf");
        String genero = dados.getString("genero");
        tipoUsuario = dados.getString("tipouser");

        //COLOCA DADOS RECEBIDOS NOS TEXTVIEW
        nomeUsuario.setText(nome);
        celularUsuario.setText(celular);
        emailUsuario.setText(email);
        enderecoUsuario.setText(endereco);
        dataNascimentoUsuario.setText(nascimento);
        cpfUsuario.setText(cpf);

//        //CONFIGURA O SWITCH GENERO DE ACORDO COM O GENERO
//        if (genero.equals("Masculino")) {
//            generoUsuario.setChecked(false);
//        } else {
//            generoUsuario.setChecked(true);
//        }



        //Colocando máscaras no Input Text
        celularUsuario.addTextChangedListener(Mask.mask(celularUsuario, Mask.FORMAT_FONE));
        cpfUsuario.addTextChangedListener(Mask.mask(cpfUsuario, Mask.FORMAT_CPF));
        dataNascimentoUsuario.addTextChangedListener(Mask.mask(dataNascimentoUsuario, Mask.FORMAT_DATE));


    }




    //endregion

    //BOTÃO SALVAR AS ALTERAÇÕES
    public void salvarAlteracoes(View view) {

        //RECUPERAR TEXTOS DOS CAMPOS
        String textoNome = nomeUsuario.getText().toString();
        String textoEmail = emailUsuario.getText().toString();
        String textoEndereco = enderecoUsuario.getText().toString();
        String textoCelular = celularUsuario.getText().toString();
        String textoDataNascimento = dataNascimentoUsuario.getText().toString();
        String textoCpf = cpfUsuario.getText().toString();


        if (!textoNome.isEmpty()) {//verifica nome
            if (!textoEndereco.isEmpty()) {//verifica endereco
                if (!textoEmail.isEmpty()) {//verifica e-mail
                    if (!textoCelular.isEmpty()) {//verifica celular
                        if (!textoDataNascimento.isEmpty()) {//verifica data nascimento
                            if (!textoCpf.isEmpty()) {//verifica o cpf


                                UserProfile usuario = new UserProfile();
                                usuario.setName(textoNome);
                                usuario.setEmail(textoEmail);
                                usuario.setAdress(textoEndereco);
                                usuario.setMobile(textoCelular);
                                usuario.setNascimento(textoDataNascimento);
                                usuario.setCpf(textoCpf);
                                usuario.setGenero(verificaGeneroUsuario());
                                usuario.setTipouser(tipoUsuario);

                                //APÓS VERIFICAÇÃO CHAMA MÉTODO PARA CADASTRAR USUÁRIO
                                cadastrarUsuario(usuario);


                            } else {
                                Toast.makeText(MeusDados.this,
                                        "Preencha o Cpf!",
                                        Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(MeusDados.this,
                                    "Preencha a Data de Nascimento!",
                                    Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(MeusDados.this,
                                "Preencha o Celular!",
                                Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(MeusDados.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(MeusDados.this,
                        "Preencha o Endereço!",
                        Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(MeusDados.this,
                    "Preencha o Nome!",
                    Toast.LENGTH_SHORT).show();

        }


    }


    public void cadastrarUsuario(final UserProfile usuario) {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (autenticacao.getCurrentUser() != null) {

            try {

                idUsuario = autenticacao.getCurrentUser().getUid();
                usuario.setId(idUsuario);
                usuario.salvar();

                //Atualizar nome no UserProfile
                UsuarioFirebase.atualizarNomeUsuario(usuario.getName());


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    //CHAMA A TELA PARA ALTERAR A SENHA DO USUÁRIO
    public void alterarSenhaUsuario(View view) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuario = autenticacao.getCurrentUser().getUid();

        //CHAMA A TELA MEUS DADOS E PASSA OS DADOS
        Intent i = new Intent(this, AlterarSenha.class);
        i.putExtra("idUsuario", idUsuario);
        i.putExtra("senha", senhaUsuario);
        i.putExtra("email", email);

        startActivity(i);

    }

    public void excluirUsuario(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Aviso!")
                .setMessage("Deseja realmente excluir sua conta ?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        excluirConta();


                    }
                }).setNegativeButton("cancelar", (dialog, which) -> {

                });
        AlertDialog dialog = builder.create();
        dialog.show();


    }


    public void excluirConta() {

        //REMOVE USUARIO DO FIREBASE AUTH (EMAIL E SENHA UTILIZADO PARA LOGAR)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(task ->{
            if (task.isSuccessful()) {
                Log.d("Aviso!", "Usuário Excluído");

            } else {
                Toast.makeText(MeusDados.this, task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

        //REMOVE INFORMAÇÕES DO USUARIO DO DATABASE
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        try {
            idUsuario = autenticacao.getCurrentUser().getUid();
            UserProfile usuario = new UserProfile();
            usuario.setId(idUsuario);
            usuario.remover();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAuth.getInstance().signOut();
        Intent m = new Intent(this, MainActivity.class);
        startActivity(m);

    }


    public String verificaGeneroUsuario() {
        return generoUsuario.isChecked() ? "Feminino" : "Masculino";
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
