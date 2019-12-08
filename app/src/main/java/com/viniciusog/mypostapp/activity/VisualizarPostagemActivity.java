package com.viniciusog.mypostapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.model.Postagem;
import com.viniciusog.mypostapp.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private TextView textPerfilPostagem, textQtdCurtidasPostagem,
            textDescricaoPostagem;
    private ImageView imagePostagemSelecionada;
    private CircleImageView imagePerfilPostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        //inicializar componentes
        inicializarComponentes();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Postagem");
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //Recuperar dados da activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            Postagem postagemSelecionada = (Postagem) bundle.getSerializable("postagemSelecionada");
            Usuario usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Exibe dados de usuário
            Uri uri = Uri.parse(usuarioSelecionado.getCaminhoFoto());
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(imagePerfilPostagem);
            textPerfilPostagem.setText(usuarioSelecionado.getNome());

            //Exibe dados da postagem
            Uri uriPostagem = Uri.parse(postagemSelecionada.getCaminhoFoto());
            Glide.with(getApplicationContext())
                    .load(uriPostagem)
                    .into(imagePostagemSelecionada);

            textDescricaoPostagem.setText(postagemSelecionada.getDescricao());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void inicializarComponentes() {
        textPerfilPostagem = findViewById(R.id.textPerfilPostagem);
        textDescricaoPostagem = findViewById(R.id.textDescricaoPostagem);
        textQtdCurtidasPostagem = findViewById(R.id.textQtdCurtidasPostagem);
        imagePerfilPostagem = findViewById(R.id.imagePerfilPostagem);
        imagePostagemSelecionada = findViewById(R.id.imagePostagemSelecionada);
    }
}
