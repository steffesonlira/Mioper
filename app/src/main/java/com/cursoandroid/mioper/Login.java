package com.cursoandroid.mioper;

//region IMPORTS

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.Arrays;
//endregion

//region CLASSE LOGIN
public class Login extends AppCompatActivity {
//region DECLARAÇÃO DE VARIÁVEIS

    //VAR DENIS
    public String Email_User, Tipo_User;
//==================================

    public EditText email;
    public EditText senha;
    private Button logar;
    private LoginButton logarFace;
    private TextView criarConta;
    private TextView esqueceuSenha;
    View viewLayout;
    private TextView txtName, txtEmail;
    DatabaseReference databaseReference;
    static final int GOOGLE_SIGN = 101;
    private static final String TAG = "GoogleActivity";
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    Button btn_login, btn_logout;
    TextView text;
    ImageView image;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    public Toast backToast;
    private long backPressedTime;

    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

//endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getSupportActionBar().hide();

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.customtoast, (ViewGroup) findViewById(R.id.custom_layout));

//Configuration Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
//End configuration Google Sign In

//region login with google+
        btn_login = findViewById(R.id.login_google);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        btn_login.setOnClickListener(v -> SignInGoogle());
//btn_logout.setOnClickListener(v -> Logout());
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
        }
//endregion

//region criação das views virtuais nomeando os campos para m ligaçãpo das views com o codigo Java
        email = findViewById(R.id.edtUsuario);
        senha = findViewById(R.id.edtSenha);
        logar = findViewById(R.id.btnLogin);
        logarFace = findViewById(R.id.login_face);
        criarConta = findViewById(R.id.link_signup);
        esqueceuSenha = findViewById(R.id.txtEsqueciSenhaId);
//endregionregion

//region login with facebook
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        logarFace.setPermissions(Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(Login.this, EsqueciSenha.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Intent intent = new Intent(Login.this, SuporteUsuario.class);
                startActivity(intent);
            }

            @Override
            public void onError(FacebookException error) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //validar permissões para uso da localizacao
        Permissoes.validarPermissoes(permissoes, this, 1);

//        logarFace.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//
//            }
//
//            @Override
//            public void onCancel() {
//
//                Toast.makeText(getApplicationContext(),"Canceled", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//            }
//
//
//        });

        //validar permissões para uso da localizacao
        Permissoes.validarPermissoes(permissoes, this, 1);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(Login.this, Principal.class);
                    startActivity(intent);
                }
            }
        };



//endregion

////region MÉTODO PARA LOGAR PADRÃO
//        logar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (email.getText().toString().equals("")) {
//                    Toast.makeText(Login.this, "Favor inserir um E-mail válido", Toast.LENGTH_SHORT).show();
//                    return;
//                } else if (senha.getText().toString().equals("")) {
//                    Toast.makeText(Login.this, "Favor inserir a senha", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
//                mAuth.signInWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//
//
//                                //IF LOGIN IS OK
//                                if (task.isSuccessful()) {
//
//                                    //GET E-MAIL FROM LOGGED USER=============
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    Email_User = user.getEmail();
//                                    //=========================================
//
//                                    //GET INFORMATION IF "M OR "P" FROM FIREBASE"===================================================
//                                    String userEmail = email.getText().toString();
//                                    String userEmailReplaced = userEmail.replace('.', '-');
//
//                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                                    DatabaseReference users = reference.child("Users").child(userEmailReplaced).child("Tipo_User");
//                                    //===============================================================================================
//
//                                    users.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                            Tipo_User = dataSnapshot.getValue().toString();
//
//                                            //IF PASSENGER, ACCESS MAIN ACTIVITY
//                                            if (Tipo_User.equals("P")) {
//                                                System.out.println("EH IGUAL A P");
//                                                final ProgressDialog progressDialog = new ProgressDialog(Login.this);
//                                                progressDialog.setIndeterminate(true);
//                                                progressDialog.setMessage("Realizando o Login...");
//                                                progressDialog.show();
//
//                                                new android.os.Handler().postDelayed(
//                                                        new Runnable() {
//                                                            public void run() {
//                                                                progressDialog.dismiss();
//                                                            }
//                                                        }, 3000);
//
//                                                Toast toastCustom = Toast.makeText(Login.this, "", Toast.LENGTH_SHORT);
//                                                toastCustom.setGravity(Gravity.CENTER, 0, 0);
//                                                toastCustom.setView(viewLayout);
//                                                toastCustom.show();
//
//                                                Intent intent = new Intent(Login.this, Principal.class);
//                                                startActivity(intent);
//
//                                                //IF DRIVER, ACCESS MAIN ACTIVITY
//                                            } else if (Tipo_User.equals("M")) {
//                                                System.out.println("EH IGUAL A M");
//                                                final ProgressDialog progressDialog = new ProgressDialog(Login.this);
//                                                progressDialog.setIndeterminate(true);
//                                                progressDialog.setMessage("Realizando o Login...");
//                                                progressDialog.show();
//
//                                                new android.os.Handler().postDelayed(
//                                                        new Runnable() {
//                                                            public void run() {
//                                                                progressDialog.dismiss();
//                                                            }
//                                                        }, 3000);
//
//                                                Toast toastCustom = Toast.makeText(Login.this, "", Toast.LENGTH_SHORT);
//                                                toastCustom.setGravity(Gravity.CENTER, 0, 0);
//                                                toastCustom.setView(viewLayout);
//                                                toastCustom.show();
//
//                                                Intent intent = new Intent(Login.this, RequisitionActivity.class);
//                                                startActivity(intent);
//                                            }
//                                        }
//
//                                        //UNUSED
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//
//                                } else {
//                                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });
//            }
//        });
////endregion

//region MÉTODO PARA CRIAR UMA NOVA SENHA
        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Cadastro.class));
            }
        });

        esqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, EsqueciSenha.class));
            }
        });
//endregion
    }




    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.error_invalid_email, Toast.LENGTH_LONG);
                }
            }
        });
    }


    //region signIn with google
    void SignInGoogle() {

        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Log.e("MYAPP", "exception", e);
                e.printStackTrace();
            }

        }
    }

    //endregion
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.addAuthStateListener(firebaseAuthListener);

        UsuarioFirebase.redirecionaUsuarioLogado(Login.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void validarLoginUsuario(View view) {

        //Recuperar textos dos campos
        String textoEmail = email.getText().toString();
        String textoSenha = senha.getText().toString();

        if (!textoEmail.isEmpty()) {//verifica e-mail
            if (!textoSenha.isEmpty()) {//verifica senha
                UserProfile usuario = new UserProfile();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);

                logarUsuario(usuario);

            } else {
                Toast.makeText(Login.this,
                        "Preencha a senha!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login.this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void logarUsuario( UserProfile usuario ){

        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        mAuth.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){

                    //Verificar o tipo de usuário logado
                    // "Motorista" / "Passageiro"
                    UsuarioFirebase.redirecionaUsuarioLogado(Login.this);

                }else {

                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuário não está cadastrado.";
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(Login.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    //region Google+  Firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {


                            }


                            Intent i = new Intent(getApplicationContext(), Principal.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_invalid_email, Toast.LENGTH_LONG);
                        }
                    }
                });
    }
//endregion



    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
// bbackToast.cancel();
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Pressione o botão voltar novamente para sair da aplicação.", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();

    }


}