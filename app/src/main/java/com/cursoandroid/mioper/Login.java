package com.cursoandroid.mioper;

//region IMPORTS

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
//endregion

//CLASSE LOGIN
public class Login extends AppCompatActivity {

    //region DECLARAÇÃO DE VARIÁVEIS
    public String Email_User, Tipo_User;
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
    //private FirebaseAuth.AuthStateListener firebaseAuthListener;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private long backPressedTime;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

//endregion

    //region Método ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LayoutInflater layoutInflater = getLayoutInflater();
        FirebaseAuth.getInstance().signOut();

        //ESCONDE O TECLADO AO INICIAR A ACTIVITY
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//region criação das views virtuais
        viewLayout = layoutInflater.inflate(R.layout.customtoast, (ViewGroup) findViewById(R.id.custom_layout));
        email = findViewById(R.id.edtUsuario);
        senha = findViewById(R.id.edtSenha);
        logar = findViewById(R.id.btnLogin);
        criarConta = findViewById(R.id.link_signup);
        esqueceuSenha = findViewById(R.id.txtEsqueciSenhaId);
        // btn_login = findViewById(R.id.login_google); NÂO UTILIZADO
        //  logarFace = findViewById(R.id.login_face); NÂO UTILIZADO
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
        }
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
//endregionregion

//region Método validarPermissoes()
        Permissoes.validarPermissoes(permissoes, this, 1);
        //endregion

/*
//region FIREBASE LISTENER
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
        //endregion FIREBASE LISTENER
*/
//region LABEL PARA CRIAR UMA NOVA CONTA no MIOPER
        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Cadastro.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

        });
//endregion

//region LABEL ESQUECI a SENHA
        esqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, EsqueciSenha.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
//endregion

//region Configuration Google Sign In NÂO UTILIZADO
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//endregion configuration Google Sign In

//region login with google+ NÂO UTILIZADO
//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
//                .Builder()
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
//        btn_login.setOnClickListener(v -> SignInGoogle());
//btn_logout.setOnClickListener(v -> Logout());

//endregion

//region login with facebook NÂO UTILIZADO

//        logarFace.setPermissions(Arrays.asList("email", "public_profile"));

//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Intent intent = new Intent(Login.this, EsqueciSenha.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onCancel() {
//                Intent intent = new Intent(Login.this, SuporteUsuario.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Intent intent = new Intent(Login.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });

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
//endregion NÂO UTILIZADO

////region MÉTODO PARA LOGAR PADRÃO NÂO UTILIZADO
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

    }
//endregion


//region HANDLE FACEBOOK NÂO UTILIZADO
//    private void handleFacebookAccessToken(AccessToken accessToken) {
//        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
//        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (!task.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), R.string.error_invalid_email, Toast.LENGTH_LONG);
//                }
//            }
//        });
//    }
//endregion

    //region METODO signIn with google NÂO UTILIZADO
    void SignInGoogle() {

        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);

    }
//endregion

//region METODO onActivityResult GOOGLE NÂO UTILIZADO
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == GOOGLE_SIGN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                Log.w(TAG, "Google sign in failed", e);
//                Log.e("MYAPP", "exception", e);
//                e.printStackTrace();
//            }
//
//        }
//    }
//endregion

    //region METODO onStart()
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //mAuth.addAuthStateListener(firebaseAuthListener);

        UsuarioFirebase.redirecionaUsuarioLogado(Login.this);
    }
//endregion

    //region METODO onRequestPermissionsResult()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }

    }
//endregion

    //region METODO alertaValidacaoPermissao()
    private void alertaValidacaoPermissao() {

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
//endregion

    //BOTAO ACESSAR
    //region METODO validarLoginUsuario()
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
//endregion


    //region METODO logarUsuario()
    public void logarUsuario(UserProfile usuario) {

        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        mAuth.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    final ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.AppCompatAlertDialogStyle);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setIcon(R.drawable.logo);
                    progressDialog.setTitle("Mioper");
                    progressDialog.setMessage("Realizando o Login...");
                    progressDialog.show();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {

                                    try {
                                        if ((progressDialog != null) && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    } catch (final IllegalArgumentException e) {
                                        // Handle or log or ignore
                                    } catch (final Exception e) {
                                        // Handle or log or ignore
                                    }


                                }
                            }, 10000);


                    //Verificar o tipo de usuário logado - "Motorista" / "Passageiro"
                    UsuarioFirebase.redirecionaUsuarioLogado(Login.this);

                } else {

                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não está cadastrado.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    } catch (Exception e) {
                        excecao = "Verifique sua conexão com a internet";
                        e.printStackTrace();
                    }
                    Toast.makeText(Login.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//endregion

    //region METODO onStop()

//endregion

//region Google+  Firebase NÂO UTILIZADO
//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//        Log.d("TAG", "firebaseAuthWithGoogle: " + account.getId());
//
//        AuthCredential credential = GoogleAuthProvider
//                .getCredential(account.getIdToken(), null);
//
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = mAuth.getCurrentUser();
//
//                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
//
//
//                            }
//
//
//                            Intent i = new Intent(getApplicationContext(), Principal.class);
//                            startActivity(i);
//                            finish();
//                        } else {
//                            Toast.makeText(getApplicationContext(), R.string.error_invalid_email, Toast.LENGTH_LONG);
//                        }
//                    }
//                });
//    }
//endregion

    //region METODO onBackPressed()
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
//endregion


}
