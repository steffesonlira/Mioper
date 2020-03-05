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
    private ImageView logo;
    private Animation topanimation, bottom_animation, middle_animation;

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
        logo = findViewById(R.id.logoMioperId);
        topanimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        middle_animation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                logo.setAnimation(topanimation);
            }
        });

        StartAnimations();

    }

    // region Animação de tela
    private void StartAnimations(){
        splashThread = new Thread(){
            @Override
            public void run(){
                try{
                    int pause = 0;
                    // Splash screen em tempo de espera
                    while (pause < 1700){
                        sleep(200);
                        pause += 100;
                    }

                    // region Verificar se existem usuários logados no app
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user != null){
                       // Toast.makeText(getApplicationContext(), "Bem vindo de Volta ao Mioper " + user.getEmail() + "!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Principal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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
