package com.gamersoci.user.gamersoci.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gamersoci.user.gamersoci.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by user on 22/11/19.
 */

public class UsuarioFirebase {
    /** pega o usuario atual*/
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();

    }
        /**pega o id do usuario*/
    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }
    /**atualiza nome do usuario*/
    public static void atualizarNomeUsuario(String nome){

        try {

            //Usuario logado no App
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName( nome )
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d("Perfil","Erro ao atualizar nome de perfil." );
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }
        /**atualiza a foto do usuario*/
    public static void atualizarFotoUsuario(Uri url){

        try {

            //Usuario logado no App
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri( url )
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d("Perfil","Erro ao atualizar a foto de perfil." );
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }
        /**pega dados do usuario que esta logado*/
    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setNome( firebaseUser.getDisplayName() );
        usuario.setId( firebaseUser.getUid() );

        if ( firebaseUser.getPhotoUrl() == null ){
            usuario.setCaminhoFoto("");
        }else{
            usuario.setCaminhoFoto( firebaseUser.getPhotoUrl().toString() );
        }

        return usuario;

    }

}
