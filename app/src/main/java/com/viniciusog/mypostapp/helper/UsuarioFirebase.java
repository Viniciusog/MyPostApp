package com.viniciusog.mypostapp.helper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.viniciusog.mypostapp.model.Usuario;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void atualizarNomeUsuario(String nome) {
        try {

            //Usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para a alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName( nome )
                    .build();

            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful() ) {
                        Log.d("Perfil", "Erro ao atualizar nome de perfil!");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void atualizarFotoUsuario(Uri url) {
        try {

            //Usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para a alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri( url )
                    .build();

            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful() ) {
                        Log.d("Perfil", "Erro ao atualizar foto de perfil!");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioLogado() {
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setId(firebaseUser.getUid());
        usuario.setNome(firebaseUser.getDisplayName());

        if ( firebaseUser.getPhotoUrl() == null ) {
            usuario.setCaminhoFoto("");
        } else {
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }
        return usuario;
    }

    //Retorna o id do usuário logado
    public static String getIdentificadorUsuario() {
            return getUsuarioAtual().getUid();
    }
}