package com.cursoandroid.mioper;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static UserProfile getDadosUsuarioLogado() {

        FirebaseUser firebaseUser = getUsuarioAtual();

        UserProfile usuario = new UserProfile();
        usuario.setId(firebaseUser.getUid());
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setName(firebaseUser.getDisplayName());

        return usuario;

    }

    public static boolean atualizarNomeUsuario(String nome) {

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar nome de perfil.");
                    }
                }
            });

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void redirecionaUsuarioLogado(final Activity activity) {

        FirebaseUser user = getUsuarioAtual();
        if (user != null) {
            Log.d("resultado", "onDataChange: " + getIdentificadorUsuario());
            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("Users")
                    .child(getIdentificadorUsuario());
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //VERIFICA SE EXISTE DADOS DO USUARIO NO DATABASE
                    if (dataSnapshot.exists()) {
                        Log.d("resultado", "onDataChange: " + dataSnapshot.toString());


                        UserProfile usuario = dataSnapshot.getValue(UserProfile.class);
                        String nomeUsuario = usuario.getName();
                        String tipoUsuario = usuario.getTipouser();
                        if (tipoUsuario.equals("M")) {
                            Intent i = new Intent(activity, Requisicoes.class);
                            activity.startActivity(i);
                        } else {
                            Intent i = new Intent(activity, Principal.class);
                            //Passa o nome de usuário para tela principal
                            i.putExtra("name", nomeUsuario);
                            activity.startActivity(i);
                        }
                    } else {
                        alerta(activity);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    //CASO EXISTIR CONTA(EMAIL E SENHA CADASTRADO), MAIS NÃO EXISTIR INFORMAÇÕES DO USUÁRIO NO DATABASE
    public static void alerta(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Aviso!")
                .setMessage("Erro ao efetuar o login. (User information was deleted on database). Entre em contato com o suporte. (mioper.suporte@outlook.com)")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void atualizarDadosLocalizacao(double lat, double lon) {

        //Define nó de local de usuário
        DatabaseReference localUsuario = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("local_usuario");
        GeoFire geoFire = new GeoFire(localUsuario);

        //Recupera dados usuário logado
        UserProfile usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Configura localização do usuário
        geoFire.setLocation(
                usuarioLogado.getId(),
                new GeoLocation(lat, lon),
                (key, error) -> {
                    if (error != null) {
                        Log.d("Erro", "Erro ao salvar local!");
                    }

                }
        );

    }

    public static String getIdentificadorUsuario() {
        return getUsuarioAtual().getUid();
    }

}
