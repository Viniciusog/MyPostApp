package com.viniciusog.mypostapp.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.adapter.AdapterPesquisa;
import com.viniciusog.mypostapp.helper.ConfiguracaoFirebase;
import com.viniciusog.mypostapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;
    private DatabaseReference usuariosRef;
    private AdapterPesquisa adapterPesquisa;

    private List<Usuario> listaUsuarios;

    public PesquisaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        inicializarComponentes(view);

        //Configurações iniciais
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        //Configurar Recycler View
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerPesquisa.setAdapter(adapterPesquisa);

        //Configurar SearchView
        searchViewPesquisa.setQueryHint("Buscar Usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String textoDigitado = s.toUpperCase();
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });
        return view;
    }

    private void pesquisarUsuarios(String textoDigitado) {
        //limpar lista de usuários
        listaUsuarios.clear();

        //pesquisa usuário caso tenha texto na pesquisa
        if (textoDigitado.length() >= 2) {

            Query query = usuariosRef.orderByChild("nome")
                    .startAt(textoDigitado)
                    .endAt(textoDigitado + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Limpar lista
                    listaUsuarios.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        listaUsuarios.add(ds.getValue(Usuario.class));
                    }

                    adapterPesquisa.notifyDataSetChanged();
                    //int total = listaUsuarios.size();
                    //Log.d("total", " (TamanhoListaUsuariosFiltrados) : " + total);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void inicializarComponentes(View view) {
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);
    }
}