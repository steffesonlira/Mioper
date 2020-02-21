package com.cursoandroid.mioper;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // region Declaração de variáveis
    Thread splashThread;
    private FirebaseAuth mAuth;

    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StartAnimations();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        StartAnimations();

    }

    // region Animação de tela
    private void StartAnimations(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animation.reset();
        ConstraintLayout layout = findViewById(R.id.linearId);
        layout.clearAnimation();
        layout.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        animation.reset();
        ImageView logo = findViewById(R.id.logoMioperId);
        logo.clearAnimation();
        logo.startAnimation(animation);

        splashThread = new Thread(){
            @Override
            public void run(){
                try{
                    int pause = 0;
                    // Splash screen em tempo de espera
                    while (pause < 3000){
                        sleep(200);
                        pause += 100;
                    }

                    // region Verificar se existem usuários logados no app
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user != null){
                        Toast.makeText(getApplicationContext(), "Bem vindo de Volta ao Mioper " + user.getEmail() + "!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Principal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);

                    }
                    //endregion

                }catch (InterruptedException e){

                }finally {
                    MainActivity.this.finish();
                }
            }
        };
        splashThread.start();


    }
    //endregion

}
