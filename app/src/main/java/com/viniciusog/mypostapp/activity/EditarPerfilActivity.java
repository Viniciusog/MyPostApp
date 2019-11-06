package com.viniciusog.mypostapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.helper.ConfiguracaoFirebase;
import com.viniciusog.mypostapp.helper.UsuarioFirebase;
import com.viniciusog.mypostapp.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imagePerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Configura a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);

        //Habilitar botão de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //inicializar os componentes
        inicializarComponentes();

        //Recuperar os dados do usuário
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText( usuarioPerfil.getDisplayName() );

        //Salvar alterações do nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Atualizar nome no perfil
                UsuarioFirebase.atualizarNomeUsuario(editNomePerfil.getText().toString());

                //Atualizar o nome diretamente no banco de dados
                usuarioLogado.setNome(editNomePerfil.getText().toString());
                usuarioLogado.atualizar();
                Toast.makeText(EditarPerfilActivity.this,
                        "Nome alterado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void inicializarComponentes() {
         imagePerfil = findViewById(R.id.imageEditarPerfil);
         textAlterarFoto = findViewById(R.id.textAlterarFotoPerfil);
         editNomePerfil = findViewById(R.id.editNomePerfil);
         buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoesPerfil);
    }
}
