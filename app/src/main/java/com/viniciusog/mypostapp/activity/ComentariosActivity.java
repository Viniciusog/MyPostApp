package com.viniciusog.mypostapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.adapter.AdapterComentario;
import com.viniciusog.mypostapp.helper.ConfiguracaoFirebase;
import com.viniciusog.mypostapp.helper.UsuarioFirebase;
import com.viniciusog.mypostapp.model.Comentario;
import com.viniciusog.mypostapp.model.Postagem;
import com.viniciusog.mypostapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {

    private RecyclerView recyclerComentarios;
    private EditText editComentario;
    private String idPostagem;
    private Usuario usuarioLogado;
    private AdapterComentario adapterComentario;
    private final List<Comentario> listaComentarios = new ArrayList<>();

    private DatabaseReference comentariosRef;
    private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Configurações iniciais
        editComentario = findViewById(R.id.editComentario);
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        recyclerComentarios = findViewById(R.id.recyclerComentarios);
        adapterComentario = new AdapterComentario(listaComentarios, getApplicationContext());

        //Configura a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);

        //Habilitar botão de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //Configura recycler view
        recyclerComentarios.setHasFixedSize(true);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentarios.setAdapter(adapterComentario);

        //Recuperar id da postagem
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idPostagem = bundle.getString("idPostagem");
        }
    }

    private void recuperarComentarios() {
        comentariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("comentarios")
                .child(idPostagem);

        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaComentarios.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    listaComentarios.add(ds.getValue(Comentario.class));
                }
                adapterComentario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener(valueEventListenerComentarios);
    }

    public void salvarcomentario(View view) {
        String textoComentario = editComentario.getText().toString();
        if (!textoComentario.equals("")) {
            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuarioLogado.getId());
            comentario.setNomeUsuario(usuarioLogado.getNome());
            comentario.setCaminhoFoto(usuarioLogado.getCaminhoFoto());
            comentario.setComentario(textoComentario);

            if (comentario.salvar()) {
                Toast.makeText(getApplicationContext(), "Comentário salvo com sucesso!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Erro ao salvar comentário!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Insira um comentário antes de salvar!",
                    Toast.LENGTH_SHORT).show();
        }

        //Limpa comentário digitado
        editComentario.setText("");
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return false;
    }
}
