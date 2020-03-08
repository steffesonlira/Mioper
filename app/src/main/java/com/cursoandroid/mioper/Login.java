package com.cursoandroid.mioper;

//region IMPORTS
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
//endregion

//region CLASSE LOGIN
public class Login extends AppCompatActivity {
    //region DECLARAÇÃO DE VARIÁVEIS
    private EditText email;
    private EditText senha;
    private Button logar;
    private LoginButton logarFace;
    private TextView criarConta;
    private TextView esqueceuSenha;
    private TextView txtName, txtEmail;
    DatabaseReference databaseReference;
    static final int GOOGLE_SIGN = 123;
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
    //endregion

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



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
        if(mAuth.getCurrentUser() != null){
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }
        //endregion

        //region criação das views virtuais nomeando os nampos para m ligaçãpo das views com o codigo Java
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

        logarFace.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

                Toast.makeText(getApplicationContext(),"Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }


        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
          @Override
          public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                Intent intent = new Intent(Login.this, Principal.class);
                startActivity(intent);
            }
          }
      };

        //endregion

        //region MÉTODO PARA LOGAR PADRÃO
        logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().toString().equals("")){
                    Toast.makeText(Login.this, "Favor inserir um E-mail válido", Toast.LENGTH_SHORT).show();
                    return;
                } else if (senha.getText().toString().equals("")) {
                    Toast.makeText(Login.this, "Favor inserir a senha", Toast.LENGTH_SHORT).show();
                    return;

                }
                mAuth.signInWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            final ProgressDialog progressDialog = new ProgressDialog(Login.this);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Realizando o Login...");
                            progressDialog.show();

                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            progressDialog.dismiss();
                                        }
                                    }, 3000);


                            Intent intent = new Intent(Login.this, Principal.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        });
        //endregion

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
                    if(!task.isSuccessful()){
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Intent intent = new Intent(Login.this, Principal.class);
            startActivity(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }
    //endregion
    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

        //region Google+  Firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        String email = txtEmail.getText().toString();
                        String password = senha.getText().toString();

                        //Criando HashMap para criação de database
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("email",email);
                        hashMap.put("password",password);

                        //firebase database instance
                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        DatabaseReference reference = database.getReference("Users");

                        reference.child(email).setValue(hashMap);

                        Log.d("TAG", "Login realizado com sucesso");
                        Intent intent = new Intent(Login.this, Principal.class);
                        startActivity(intent);

                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w("TAG", "Falha na realização do Login", task.getException());
                        Toast.makeText(this, "Falha na Realização do Login", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                        return;//k
                    }

                });
    }
    //endregion

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            String name = user.getDisplayName();
            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());

            text.append("Info : \n");
            text.append(name + "\n");
            text.append(email);

            //Picasso.get().load(photo).into(image);
            btn_login.setVisibility(View.INVISIBLE);
            btn_logout.setVisibility(View.VISIBLE);

        } else {

            text.setText("Firebase Login");
            Picasso.get().load(R.drawable.ic_firebase_logo).into(image);
            btn_login.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.INVISIBLE);

        }
    }

    void Logout(){

        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));

                }



    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken == null) {

                txtName.setText("");
                txtEmail.setText("");
                Toast.makeText(Login.this, "Usuário fez Logout", Toast.LENGTH_SHORT).show();
            }else{
                loaduserProfile(currentAccessToken);
            }
        }
    };

    private void loaduserProfile(AccessToken newAccessToken) {

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    txtEmail.setText(email);
                    txtName.setText(first_name + " " + last_name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    //Glide.with(MainActivity.this).load(image_url).into(circleImageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onBackPressed() {
           if(backPressedTime + 2000 > System.currentTimeMillis()){
               // bbackToast.cancel();
               super.onBackPressed();
               finish();
            }
           else{
               Toast.makeText(getApplicationContext(), "Pressione o botão voltar novamente para sair da aplicação.",Toast.LENGTH_SHORT).show();
           }
            backPressedTime = System.currentTimeMillis();

        }


}