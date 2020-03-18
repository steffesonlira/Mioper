package com.cursoandroid.mioper.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by jamiltondamasceno
 */


public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getEmail();
    }

//    public static Usuario getDadosUsuarioLogado(){
//
//        FirebaseUser firebaseUser = getUsuarioAtual();
//
//        Usuario usuario = new Usuario();
//        usuario.setId( firebaseUser.getUid() );
//        usuario.setEmail( firebaseUser.getEmail() );
//        usuario.setNome( firebaseUser.getDisplayName() );
//
//        return usuario;
//
//    }

//    public static boolean atualizarNomeUsuario(String nome){
//
//        try {
//
//            FirebaseUser user = getUsuarioAtual();
//            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
//                    .setDisplayName( nome )
//                    .build();
//            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if( !task.isSuccessful() ){
//                        Log.d("Perfil", "Erro ao atualizar nome de perfil.");
//                    }
//                }
//            });
//
//            return true;
//
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }

//    }

//    public static void redirecionaUsuarioLogado(final Activity activity){
//
//        FirebaseUser user = getUsuarioAtual();
//        if(user != null ){
//            Log.d("resultado", "onDataChange: " + getIdentificadorUsuario());
//            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
//                    .child("usuarios")
//                    .child( getIdentificadorUsuario() );
//            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.d("resultado", "onDataChange: " + dataSnapshot.toString() );
//                    Usuario usuario = dataSnapshot.getValue( Usuario.class );
//
//                    String tipoUsuario = usuario.getTipo();
//                    if( tipoUsuario.equals("M") ){
//                        Intent i = new Intent(activity, RequisicoesActivity.class);
//                        activity.startActivity(i);
//                    }else {
//                        Intent i = new Intent(activity, PassageiroActivity.class);
//                        activity.startActivity(i);
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//
//    }



}
