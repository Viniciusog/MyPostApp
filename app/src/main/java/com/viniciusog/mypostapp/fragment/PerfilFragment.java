package com.viniciusog.mypostapp.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.activity.EditarPerfilActivity;
import com.viniciusog.mypostapp.adapter.AdapterGrid;
import com.viniciusog.mypostapp.helper.ConfiguracaoFirebase;
import com.viniciusog.mypostapp.helper.UsuarioFirebase;
import com.viniciusog.mypostapp.model.Postagem;
import com.viniciusog.mypostapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {


    private ProgressBar progressPerfil;
    private GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;
    private CircleImageView circleFotoPerfil;
    private Usuario usuarioLogado;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference postagensUsuarioLogadoRef;

    private AdapterGrid adapterGrid;

    private ValueEventListener valueEventListenerPerfil;


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //configuração inicial
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = firebaseRef.child("usuarios");
        postagensUsuarioLogadoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("postagens")
                .child(usuarioLogado.getId());

        //configuraçoes dos componentes
        inicializarComponentes(view);

        //Abre tela de edição do perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(intent);
            }
        });

        //inicia o universal image loader
        inicializarImageLoader();

        //carrega as fotos das postagens do usuário logado
        carregarFotosPostagem();

        return view;
    }

    //Inicia o universal image loader
    private void inicializarImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()).build(); // default.build()

        ImageLoader.getInstance().init(config);
    }

    private void carregarFotosPostagem() {
        //Recupera as fotos postadas pelo usuário selecionado
        postagensUsuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Configurar tamanho do grid / pixel largura aparelho == pixel largura grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoiImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoiImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Postagem postagem = ds.getValue(Postagem.class);
                    urlFotos.add(postagem.getCaminhoFoto());
                }
                //Quantidade de postagens feitas pelo usuário selecionado
                textPublicacoes.setText(String.valueOf(urlFotos.size()));

                //Configurar adapter
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes(View view) {
        progressPerfil = view.findViewById(R.id.progressGridPerfil);
        textPublicacoes = view.findViewById(R.id.textPublicacoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        circleFotoPerfil = view.findViewById(R.id.imagePerfil);
    }

    private void recuperarDadosDoUsuarioLogado() {
        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getId());
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarioLogado = dataSnapshot.getValue(Usuario.class);
                textPublicacoes.setText(String.valueOf(usuarioLogado.getPostagens()));
                textSeguindo.setText(String.valueOf(usuarioLogado.getSeguindo()));
                textSeguidores.setText(String.valueOf(usuarioLogado.getSeguidores()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarFotoUsuario() {

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // Não está funcionando e não sei porque
        /* Recuperar foto do usuário
        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if (caminhoFoto != null || !caminhoFoto.equals("")) {
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity())
                    .load(url)
                    .into(circleFotoPerfil);
        } else {
            circleFotoPerfil.setImageResource(R.drawable.avatar);
        }*/

        //Recuperando e exibindo foto de perfil
        Uri url = UsuarioFirebase.getUsuarioAtual().getPhotoUrl();
        if (url != null) {
            Glide.with(getActivity())
                    .load(url)
                    .into(circleFotoPerfil);
        } else {
            circleFotoPerfil.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarDadosDoUsuarioLogado();
        recuperarFotoUsuario();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }
}