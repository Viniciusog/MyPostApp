package com.viniciusog.mypostapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.helper.ConfiguracaoFirebase;
import com.viniciusog.mypostapp.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText editNome, editSenha, editEmail;
    private Button botaoCadastrar;
    private ProgressBar progressBar;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        //Cadastrar Usuário
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoNome = editNome.getText().toString();
                String textoEmail = editEmail.getText().toString();
                String textoSenha = editSenha.getText().toString();

                if ( !textoNome.isEmpty()) {
                    if ( !textoEmail.isEmpty()) {
                        if ( !textoSenha.isEmpty()) {

                            usuario = new Usuario();
                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textoSenha );
                            cadastrarUsuario();

                        } else {
                            Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastroActivity.this, "Preencha um email!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this, "Preencha um nome!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void cadastrarUsuario() {
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()

        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ) {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(CadastroActivity.this,
                            "Cadastrado com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    startActivity( new Intent(CadastroActivity.this, MainActivity.class));
                    finish();

                } else {
                     progressBar.setVisibility(View.GONE);

                     String erroExcecao = "";

                     try {
                         throw task.getException();
                     } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte.";
                     } catch (FirebaseAuthInvalidCredentialsException e) {
                         erroExcecao = "Digite um e-mail válido.";
                     } catch (FirebaseAuthUserCollisionException e) {
                         erroExcecao = "Esta conta já está contratada!";
                     } catch (Exception e ){
                         erroExcecao = "Erro ao cadastrar usuário! " + e.getMessage();
                         e.printStackTrace();
                     }

                     Toast.makeText(CadastroActivity.this,
                             "Erro" + erroExcecao,
                             Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void inicializarComponentes() {

        editNome = findViewById(R.id.editCadastroNome);
        editEmail = findViewById(R.id.editCadastroEmail);
        editSenha = findViewById(R.id.editCadastroSenha);

        botaoCadastrar = findViewById(R.id.buttonCadastrar);
        progressBar = findViewById(R.id.progressBarCadastro);

    }
}
