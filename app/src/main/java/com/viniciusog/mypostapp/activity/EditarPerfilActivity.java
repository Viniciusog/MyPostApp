package com.viniciusog.mypostapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.helper.ConfiguracaoFirebase;
import com.viniciusog.mypostapp.helper.UsuarioFirebase;
import com.viniciusog.mypostapp.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;
    private StorageReference storageRef;
    private String identificadorUsuario;

    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

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
        editNomePerfil.setText(usuarioPerfil.getDisplayName().toUpperCase());

        //Recuperando e exibindo foto de perfil
        Uri url = usuarioPerfil.getPhotoUrl();
        if (url != null) {
            Glide.with(EditarPerfilActivity.this)
                    .load(url)
                    .into(imageEditarPerfil);
        } else {
            imageEditarPerfil.setImageResource(R.drawable.avatar);
        }

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

        //Alterar foto do usuário
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                //Verificar se conseguiremos abrir a galeria
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {

                //Seleção apenas para a galeria
                switch (requestCode) {
                    case SELECAO_GALERIA: {
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                        break;
                    }
                }

                //Caso o usuário tenha escolhido uma imagem
                if (imagem != null) {
                    imageEditarPerfil.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no Firebase
                    StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Recuperar local da foto
                            Uri url = taskSnapshot.getDownloadUrl();
                            atualizarFotoUsuario(url);

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Sucesso ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri url) {
        //Atualizar a foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        //Atualizar foto no Firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(EditarPerfilActivity.this,
                "Sua foto foi atualizada!", Toast.LENGTH_SHORT).show();
    }

    public void inicializarComponentes() {
        imageEditarPerfil = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFotoPerfil);
        editNomePerfil = findViewById(R.id.editNomePerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoesPerfil);
    }
}
