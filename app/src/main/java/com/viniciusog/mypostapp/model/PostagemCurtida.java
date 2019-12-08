package com.viniciusog.mypostapp.model;

import com.google.firebase.database.DatabaseReference;
import com.viniciusog.mypostapp.helper.ConfiguracaoFirebase;

import java.util.HashMap;

public class PostagemCurtida {

    public int qtdCurtidas = 0;
    public Feed feed;
    public Usuario usuario;

    public PostagemCurtida() {

    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //Objeto usuário
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto());

        DatabaseReference pCurtidas = firebaseRef.child("postagens-curtidas")
                .child(feed.getId()) //id_postagem
                .child(usuario.getId()); //id_usuario
        pCurtidas.setValue(dadosUsuario);

        atualizarQtd(1);
    }

    public void atualizarQtd(int valor) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference pCurtidas = firebaseRef.child("postagens-curtidas")
                .child(feed.getId()) //id_postagem
                .child("qtdCurtidas"); //qtd curtidas
        setQtdCurtidas(getQtdCurtidas() + valor);
        pCurtidas.setValue(getQtdCurtidas());
    }

    public void remover() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //tirar usuário que deu deslike
        DatabaseReference pCurtidas = firebaseRef.child("postagens-curtidas")
                .child(feed.getId()) //id_postagem
                .child(usuario.getId()); //qtd curtidas
        pCurtidas.removeValue();

        atualizarQtd(-1);
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
